package com.lingx.m60.server.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lingx.jt808.core.Constants;
import com.lingx.jt808.core.service.RedisService;
import com.lingx.jt808.core.utils.Utils;
import com.lingx.jt808.server.service.JT808ServerConfigService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component//@GELOC,2024-02-21T16:08:56.00,39.8498030983,116.3236076683,296.5260,0.0000,0.0000,0.0000,15,5,12.40,27.242,58.330,2,0.1500,219.0900,M6017322000013,,,*5E
public class Msg0x02 implements IMsgHandler{
	public static Cache<String, String> DL_CACHE=CacheBuilder.newBuilder().maximumSize(100000).expireAfterWrite(20, TimeUnit.MINUTES).build();
	
	@Autowired
	private JT808ServerConfigService configService;
	@Autowired
	protected RedisService redisService;
	@Override
	public int getMsgId() {
		return 0x02;
	}

	@Override
	public void handle(String data, String tid, int msgId, int msgSn, ChannelHandlerContext ctx, boolean isVersion,
			byte[] bytes) throws Exception {
		String array[]=data.split(",");
		if("OK".equals(array[3])) {
			String dl=array[4].split("[*]")[0];
			DL_CACHE.put(tid, dl);
			//System.out.println(dl);
		}
	}
	
	public static void main(String args[]) {
		Msg0x02 obj=new Msg0x02();
		String hexstring="4047454C4F432C323032342D30332D32385431353A34333A32362E30302C33392E383438393938343030302C3131362E333235393230383030302C35392E393936302C302E303030302C302E303030302C302E303030302C31302C322C352E36302C31322E3534302C32392E3432362C312C302E303030302C302E303030302C4D363031373332323030303031332C2C2C2A30440D0A";
		try {
			String temp=new String(Utils.hexToBytes(hexstring));
			System.out.println(temp);
			obj.handle(temp, null, 0, 0, null, false, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
