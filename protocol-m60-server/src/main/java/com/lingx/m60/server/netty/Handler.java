package com.lingx.m60.server.netty;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lingx.jt808.core.IJT808Cache;
import com.lingx.jt808.core.event.JT808OfflineEvent;
import com.lingx.jt808.core.event.JT808OnlineEvent;
import com.lingx.jt808.core.support.MyByteBuf;
import com.lingx.jt808.core.utils.Utils;
import com.lingx.jt808.server.utils.WebsocketUtils;
import com.lingx.m60.server.msg.IMsgHandler;
import com.lingx.m60.server.service.ServerConfigService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

@Sharable
@Component
public class Handler extends SimpleChannelInboundHandler<byte[]> {
	
	public static Cache<String, Long> SEND_CMD_CACHE=CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(10, TimeUnit.MINUTES).build();
	
	/**
	 * 黑名单集合
	 */
	public static Set<String> BLACKLIST=Collections.synchronizedSet(new HashSet<>());
	@Autowired
	private ApplicationContext spring;
	@Autowired
	private List<IMsgHandler> listMsgHandler;
	@Autowired
	private ServerConfigService serverConfigService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private Set<Integer> yesres30=new HashSet<>();
	@PostConstruct
	public void init() {
		yesres30.add(0x80);
		yesres30.add(0x81);
		yesres30.add(0x69);
		yesres30.add(0x6B);
		yesres30.add(0x6F);
		yesres30.add(0xA1);
		yesres30.add(0xA3);
		yesres30.add(0xA4);
		yesres30.add(0xA6);
		yesres30.add(0xA7);
		yesres30.add(0xA8);
		this.reloadBlacklist();//加载黑名单
	}
	@Override
	public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		//System.out.println(Utils.bytesToHex(msg));
		if(this.serverConfigService!=null&&"true".contains(this.serverConfigService.getSaveOriginalHexstring())) {
			if(msg.length<500) {
				this.jdbcTemplate.update("insert into tgps_original(data,ts) values(?,?)",Utils.bytesToHex(msg)+":"+ctx.channel().remoteAddress().toString(),Utils.getTime());
			}
		}
		
		  if(WebsocketUtils.isPush) { WebsocketUtils.push(Utils.bytesToHex(msg)); }
		String str=new String(msg);
		 

		try {
			int msgId =0;
			String tid="";
		if(str.startsWith("@GELOC")) {
			msgId =0x01;
			String array[]=str.split(",");
			tid = array[16];
			
			if(ctx.channel().attr(AttributeKey.valueOf("TID1")).get()==null)
			ctx.channel().attr(AttributeKey.valueOf("TID1")).set(tid);
			
			if(SEND_CMD_CACHE.getIfPresent(tid)==null) {
				System.out.println("下发指令:GET,DEVICE.POWER_LEVEL");
				ctx.channel().writeAndFlush("GET,DEVICE.POWER_LEVEL\n".getBytes());
				SEND_CMD_CACHE.put(tid, System.currentTimeMillis());
			}
		}else if(str.startsWith("@GNSS")){
			msgId =0x02;
			System.out.println(str);
		}else {
			
			System.out.println(str);
			return;
		}
		Object obj=ctx.channel().attr(AttributeKey.valueOf("TID1")).get();
		if(obj!=null)tid=obj.toString();
			if(BLACKLIST.contains(tid)) {
				//ctx.close();//在黑名单内，直接断开连接，有可能是转发的所以不断开
				return;
			}
			boolean isVersion=false;
			if(ctx!=null&&!SetsUtils.has(ctx.channel(), tid)) {
				SetsUtils.add(ctx.channel(), tid);
				JT808OnlineEvent event=new JT808OnlineEvent(spring,tid);
				spring.publishEvent(event);
				
			}
			if(ctx!=null)IJT808Cache.SESSIONS.put(tid, ctx.channel());
			//int msgSn = buff.readUnsignedShort();// 消息流水号
			
			

			{//校检码处理
				IMsgHandler msgHandler=null;
				for(IMsgHandler tmp:this.listMsgHandler) {
					if(tmp.getMsgId()==msgId) {
						msgHandler=tmp;break;
					}
				}
				if(msgHandler!=null) {
					try {
						msgHandler.handle(str, tid, msgId, 0, ctx,isVersion,msg);
					} catch (Exception e) {
						System.out.println(System.currentTimeMillis()+":"+Utils.bytesToHex(msg));
						e.printStackTrace();
					}
				}else {
					System.out.println(String.format("没有对应的处理器：%04X,%s,%s",msgId,tid,Utils.bytesToHex(msg)));
				}

				
			}
		} catch (Exception e) {
			System.out.println(System.currentTimeMillis()+":"+Utils.bytesToHex(msg));
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("==============长连接失效===============");
		super.channelInactive(ctx);
		if(ctx.channel().hasAttr(AttributeKey.valueOf("TID"))) {
			Set<String> sets=SetsUtils.get(ctx.channel());
			for(String tid:sets) {
				JT808OfflineEvent event=new JT808OfflineEvent(spring,tid);
				spring.publishEvent(event);
				IJT808Cache.SESSIONS.invalidate(tid);
			}
			
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("==============长连接接入===============");
		super.channelActive(ctx);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
		//throwable.printStackTrace();
		//ctx.close();
	}
	public void setListMsgHandler(List<IMsgHandler> listMsgHandler) {
		this.listMsgHandler = listMsgHandler;
	}
	@Scheduled(cron="0 0/30 * * * ?")//每30分钟计算
	public void reloadBlacklist() {
		BLACKLIST.clear();
		try {
			List<Map<String,Object>> list=this.jdbcTemplate.queryForList("select tid from tgps_blacklist");
			for(Map<String,Object> map:list) {
				BLACKLIST.add(map.get("tid").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
