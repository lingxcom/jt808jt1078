package com.lingx.gt06.server.msg;

import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@Component
public class Msg0x00 implements IMsgHandler{

	@Override
	public int getMsgId() {
		return 0;
	}

	@Override
	public void handle(ByteBuf data, String tid, int msgId, int msgSn, ChannelHandlerContext ctx, boolean isVersion,
			byte[] bytes,String sn) throws Exception {
		
	}

}
