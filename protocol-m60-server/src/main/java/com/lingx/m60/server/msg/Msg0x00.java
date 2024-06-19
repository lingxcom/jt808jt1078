package com.lingx.m60.server.msg;

import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component//@GELOC,2024-02-21T16:08:56.00,39.8498030983,116.3236076683,296.5260,0.0000,0.0000,0.0000,15,5,12.40,27.242,58.330,2,0.1500,219.0900,M6017322000013,,,*5E
public class Msg0x00 implements IMsgHandler{

	@Override
	public int getMsgId() {
		return 0;
	}

	@Override
	public void handle(String data, String tid, int msgId, int msgSn, ChannelHandlerContext ctx, boolean isVersion,
			byte[] bytes) throws Exception {
		
	}

}
