package com.lingx.gt06.server.netty;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lingx.gt06.server.msg.IMsgHandler;
import com.lingx.gt06.server.service.ServerConfigService;
import com.lingx.jt808.core.IJT808Cache;
import com.lingx.jt808.core.event.JT808OfflineEvent;
import com.lingx.jt808.core.event.JT808OnlineEvent;
import com.lingx.jt808.core.support.MyByteBuf;
import com.lingx.jt808.core.utils.Utils;
import com.lingx.jt808.server.utils.WebsocketUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

@Sharable
@Component
public class Handler extends SimpleChannelInboundHandler<byte[]> {
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
	private boolean b=true;
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
		if(msg[0]!=0x78||msg[1]!=0x78)return ;
		
		  if(WebsocketUtils.isPush) { WebsocketUtils.push(Utils.bytesToHex(msg)); }

			MyByteBuf buff = new MyByteBuf(msg);
		try {
			
			
			buff.readByte();buff.readByte();
			int length = buff.readUnsignedByte();
			int msgId = buff.readUnsignedByte();
			ByteBuf content = buff.readByteBuf(length-5);
			String sn=buff.readStringBCD(2);
			String tid = null;
			Object obj=ctx.channel().attr(AttributeKey.valueOf("TID1")).get();
			if(obj!=null)tid=obj.toString();
			
			
			if(BLACKLIST.contains(tid)) {
				//ctx.close();//在黑名单内，直接断开连接，有可能是转发的所以不断开
				return;
			}
			boolean isVersion=false;
			if(tid!=null&&ctx!=null&&!SetsUtils.has(ctx.channel(), tid)) {
				SetsUtils.add(ctx.channel(), tid);
				JT808OnlineEvent event=new JT808OnlineEvent(spring,tid);
				spring.publishEvent(event);
			}
			if(tid!=null&&ctx!=null)IJT808Cache.SESSIONS.put(tid, ctx.channel());
			//int msgSn = buff.readUnsignedShort();// 消息流水号
			
			buff.readByte();
			buff.readByte();
			{//校检码处理
				IMsgHandler msgHandler=null;
				for(IMsgHandler tmp:this.listMsgHandler) {
					if(tmp.getMsgId()==msgId) {
						msgHandler=tmp;break;
					}
				}
				if(msgHandler!=null) {
					try {
						msgHandler.handle(content, tid, msgId, 0, ctx,isVersion,msg,sn);
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
		}finally {
			buff.release();
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
