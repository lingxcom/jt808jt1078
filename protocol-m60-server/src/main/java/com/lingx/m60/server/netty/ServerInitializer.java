package com.lingx.m60.server.netty;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

@Component
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
	@Autowired
	private Handler gpsHandler;

	@PostConstruct
	public void init() {

	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast("decoder", new ByteArrayDecoder());
		// 字符串解码 和 编码
		pipeline.addLast(new ReadTimeoutHandler(360));
		pipeline.addLast("encoder", new ByteArrayEncoder());

		// 自己的逻辑Handler
		pipeline.addLast("handler", gpsHandler);
	}

}
