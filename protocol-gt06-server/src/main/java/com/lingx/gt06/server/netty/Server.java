package com.lingx.gt06.server.netty;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lingx.gt06.server.service.ServerConfigService;
import com.lingx.jt808.core.AbstractJT808ThreadService;
import com.lingx.jt808.core.IJT808ThreadService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

@Component
public class Server extends AbstractJT808ThreadService implements IJT808ThreadService, Runnable {
	private ChannelFuture future = null;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	@Autowired
	private ServerConfigService configService;
	@Autowired
	private ServerInitializer lhcxServerInitializer;

	public void startServer() throws Exception {

		int port=Integer.parseInt(this.configService.getServerPort());
		//System.out.println("Start JT808Server TCP port:"+port);
			bossGroup = new NioEventLoopGroup(0,new DefaultThreadFactory("LhcxServerBossGroup"));
			workerGroup = new NioEventLoopGroup(0,new DefaultThreadFactory("LhcxServerWorkerGroup"));

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 2048, 65536));
			b.childHandler(lhcxServerInitializer);

			// 服务器绑定端口监听
			future = b.bind(port).sync();
			future.channel().closeFuture().sync();
			// 可以简写为
			/* b.bind(portNumber).sync().channel().closeFuture().sync(); */
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@PreDestroy
	public void stop() {
		try {
			// 监听服务器关闭监听
			if (future != null)
				future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	@Override
	public void run() {
		try {
			this.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		
		return "GT06-Server -> GT06网关服务:"+this.configService.getServerPort();
	}

}
