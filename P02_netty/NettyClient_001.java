package main.java.netty;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClient_001 {
	public static void main(String... args) throws Exception {
		EchoClient.main(args);
	}
}

class EchoClient {
	public static void main(String... args) throws Exception {

		class EchoClientHandler extends ChannelInboundHandlerAdapter {
			@Override
			public void channelActive(ChannelHandlerContext ctx) {
				System.out.print("01. channelActive > ");
				String sendMessage = "Hello netty";

				ByteBuf messageBuffer = Unpooled.buffer();
				messageBuffer.writeBytes(sendMessage.getBytes());
				
				StringBuilder builder = new StringBuilder("전송한 문자열 [").append(sendMessage).append("]");
				System.out.println(builder.toString());
				ctx.writeAndFlush(messageBuffer);
			}
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) {
				System.out.print("02. channelRead > ");
				String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
				StringBuilder builder = new StringBuilder("수신한 문자열 [").append(readMessage).append("]");
				System.out.println(builder.toString());
			}
			@Override
			public void channelReadComplete(ChannelHandlerContext ctx) {
				System.out.print("03. channelReadComplete > ");
				ctx.close();
			}
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
				System.out.println("에러가 발생했군요.");
				cause.printStackTrace();
				ctx.close();
			}
		}

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline p = ch.pipeline();
					p.addLast(new LoggingHandler(LogLevel.INFO));
					p.addLast(new EchoClientHandler());
				}
			});
			ChannelFuture f = b.connect("localhost", 8888).sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
}
