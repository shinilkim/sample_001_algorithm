package main.java.netty;

import java.nio.ByteBuffer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class NettyServer_004 {
	public static void main(String... args) throws Exception {
//		args = new String[] { "EchoServerHandler" };
//		args = new String[] { "EchoServerHandlerWithFuture" };
//		EchoServer.main(args);

		EchoServerWithFuture.main(args);
	}
}

class EchoServer {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(EchoServer.class);

	public static void main(String... args) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							if ("EchoServerHandlerWithFuture".equals(args[0])) {
								p.addLast(new EchoServerHandlerWithFuture());
							} else {
								p.addLast(new EchoServerHandler());
							}
						}
					});
			int port = 8888;
			logger.info("netty server start at 127.0.0.1:" + port);
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			Future f1 = workerGroup.shutdownGracefully();
			f1.sync();
			Future f2 = bossGroup.shutdownGracefully();
			f2.sync();
		}
	}
}

class EchoServerWithFuture {
	public static void main(String... args) throws Exception {
		bufferStatusCheck();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new EchoServerHandlerWithFuture());
						}
					});
			ChannelFuture bindFuture = b.bind(8888);
			System.out.println("Bind 시작");
			bindFuture.sync();
			System.out.println("Bind 완료");

			Channel serverChannel = bindFuture.channel();
			ChannelFuture closeFuture = serverChannel.closeFuture();
			closeFuture.sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	private static void bufferStatusCheck() {
		System.out.println("------------------------");
		java.nio.ByteBuffer firstBuffer = java.nio.ByteBuffer.allocate(11);
		System.out.println("바이트 버퍼 초기값: " + firstBuffer);

		byte[] source = "Hello world".getBytes();
		firstBuffer.put(source);
		System.out.println("11바이트 기록 후: " + firstBuffer);

		byteOverflow();
	}

	private static void byteOverflow() {
		System.out.println("------------------------");
		ByteBuffer firstBuffer = ByteBuffer.allocate(11);
		System.out.println("바이트 버퍼 초기값: " + firstBuffer);

		byte[] source = "Hello world!".getBytes();
		try {
			for (byte item : source) {
				firstBuffer.put(item);
				System.out.println("현재 상태: " + firstBuffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		aaa();
	}

	private static void aaa() {
		System.out.println("------------------------");
		ByteBuffer firstBuffer = ByteBuffer.allocate(11);
		System.out.println("바이트 버퍼 초기값: " + firstBuffer);

		firstBuffer.put((byte) 1);
		System.out.println(firstBuffer.get());// get을 했는데, postion 이 변경되었다.
		System.out.println(firstBuffer);
	}
}

@Sharable
class EchoServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// 수신된 데이터를 클라이언트 소켓 버퍼에 기록하고 버퍼의 데이터를 채널로 전송하는 비동기 메서드인
		// writeAndFlush를 호출하고 ChannelFuture 객체를 돌려 받는다.
		ChannelFuture channelFuture = ctx.writeAndFlush(msg);
		// ChannelFuture 객체에 채널을 종료하는 리스너를 등록한다.
		// ChannelFutureListener.CLOSE 리스너는 네티가 제공하는 기본 리스너로서
		// ChannelFuture 객체가 완료 이벤트를 수신할 때 수행된다.
		channelFuture.addListener(ChannelFutureListener.CLOSE);

		// [ChannelFutureListener.CLOSE] 작업 성공 여부와 상관없이 수행된다.
		// ChannelFuture 객체가 작업 완료 이벤트를 수신했을 때 ChannelFuture 객체에 포함된 채널을 다는다.

		// [ChannelFutureListener.CLOSE_ON_FAILURE]
		// ChannelFuture 객체가 완료 이벤트를 수신하고 결과가 실패일 때 ChannelFuture 객체에 포함된 채널을 닫느다.

		// [ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE]
		// ChannelFutre 객체가 완료 이벤트를 수신하고 결과가 실패일 때 채널 예외 이벤트를 발생시킨다.
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

@Sharable
class EchoServerHandlerWithFuture extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ChannelFuture channelFuture = ctx.writeAndFlush(msg);
		// 01. 네티가 수신한 msg 객체는 ByteBuf 객체이다. 또한 클라이언트로부터 수신한 데이터를
		// 클라이언트로 되돌려주므로 전송한 데이터의 크기는 msg 객체의 크기와 같다.
		// msg 객체에 저장된 데이터의 크기를 final 지역 변수에 저장한다.
		final int writeMessageSize = ((ByteBuf) msg).readableBytes();
		// 02.사용자 정의 채널 리스너를 생성하여 ChannelFuture 객체에 할당한다.
		channelFuture.addListener(new ChannelFutureListener() {
			@Override // 03. ChannelFuture 객체에서 발생하는 작업 완료 이벤트 메서드로서 사용자 정의 채널 리스너 구현에 포함되어함.
			public void operationComplete(ChannelFuture future) throws Exception {
				System.out.println("전송할 Bytes: " + writeMessageSize);// 04. 전송한 데이터 크기
				future.channel().close();// 05. ChannelFuture 객체에 포함된 채널을 가져와서 채널 닫기 이벤트를 발생시킨다.
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
