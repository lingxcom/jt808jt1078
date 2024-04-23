package com.lingx.gt06.server.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IMsgHandler {
	public int getMsgId();

	public void handle(ByteBuf data,String tid,int msgId,int msgSn,ChannelHandlerContext ctx,boolean isVersion,byte[] bytes,String sn)throws Exception;

}
