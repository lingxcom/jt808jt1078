package com.lingx.m60.server.msg;

import io.netty.channel.ChannelHandlerContext;

public interface IMsgHandler {
	public int getMsgId();

	public void handle(String data,String tid,int msgId,int msgSn,ChannelHandlerContext ctx,boolean isVersion,byte[] bytes)throws Exception;

}
