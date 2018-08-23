package main.java.netty;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer_001 {
	public static void main(String... args) throws Exception {
		args = new String[]{"EchoServerHandler"};
		DiscardServer.main(args);
	}
}

/**
 * [002] 속도가 가장 빠르지만, 리눅스 운영체제에서만 동작함(윈도우 환경에서 실행하면 에러발생
 */
class EpollEchoServer {
	public static void main(String...args) throws Exception {
		EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
		EventLoopGroup workerGroup = new EpollEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(EpollServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();
					//p.addLast(new EchoServerHandler());
				}
			});
			ChannelFuture f = b.bind(8888).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

/**
 * [001] 간단한 netty 서버 구현
 */
class DiscardServer {
	/**
	 * 네티의 이벤트 루프, 부트스트랩, 채널 파이프라인, 핸들러 등의 클래스의 초기화
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... args) throws Exception {

		class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {
			@Override
			public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
				System.out.println("아무것도 하지 않음");
				// System.out.println(1/0);// exceptionCaught 테스트
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
				System.out.println("에러가 발생했군요.");
				cause.printStackTrace();
				ctx.close();
			}
		}

		class EchoServerHandler extends ChannelInboundHandlerAdapter {
			@Override /*데이터수신이벤트 - 데이터의 수신이 이루어졌을때 자동으로 호출*/
			public void channelRead(ChannelHandlerContext ctx, Object msg) {
				String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());// 네티의 바이트버퍼 객체로부터 문자열 읽기
				StringBuilder builder = new StringBuilder("수신한 문자열 [").append(readMessage).append("]");
				System.out.println(builder.toString());
				ctx.write(msg);
			}
			@Override /* ChannelHandlerContext 인터페이스 객체로 채널 파이프라인에 대한 이벤트 처리 */
			public void channelReadComplete(ChannelHandlerContext ctx) {
				ctx.flush();
			}
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
				System.out.println("에러가 발생했군요.");
				cause.printStackTrace();
				ctx.close();
			}
		}
		/* 01. [Client 연결] 생성자에 입력된 스레드수가 1 이므로 단인 스레드로 동작(스레드수는 하드웨어 CPU 코어 수의 2배) */
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);/*OioEventLoopGroup로 하면 블러킹 모드*/
		/* 02. [Client 송/수신] 생성자에 인수가 없으므로 CPU 코아 수에 따른 스레드수가 설정 */
		EventLoopGroup workerGroup = new NioEventLoopGroup();/*OioEventLoopGroup로 하면 블러킹 모드*/
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)  /*bossGroup:부모스레드, workerGroup:자식스레드*/
			.channel(NioServerSocketChannel.class) /*NIO 모드로 설정(OioServerSocketChannle.class로 하면 블로컹 모드)*/
			.option(ChannelOption.SO_BACKLOG, 1) /* Server option */
			.childOption(ChannelOption.SO_LINGER, 0) /* client option(모두 전송하고 바로 연결해제) */
			.childHandler(new ChannelInitializer<SocketChannel>() { /*익명클래스로 채널 초기화 */
				@Override
				public void initChannel(SocketChannel ch) {
					ChannelPipeline p = ch.pipeline();/*채널 파이프라인 객체 생성*/
					// 수신된 데이터를 처리할 핸들러
					if ("EchoServerHandler".equals(args[0])) {
						p.addLast(new EchoServerHandler());// 에코 기능 Y
					} else {
						p.addLast(new DiscardServerHandler());// 에코 기능 N
					}
					System.out.println("DiscardServer init");
				}
			});

			ChannelFuture f = b.bind(8888).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}

	}

}
