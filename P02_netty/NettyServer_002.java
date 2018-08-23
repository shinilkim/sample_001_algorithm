package main.java.netty;

import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class NettyServer_002 {
	public static void main(String... args) throws Exception {
//		args = new String[]{"EchoServerV1Handler"};// 데이터를 수신하자마다 상대방에게 데이터를 전송하는 예제
//		args = new String[]{"EchoServerV2Handler"};// 소켓 채널에세 더 이상 읽어들일 데이터가 없을 때 데이터는 전송하는 예제
//		args = new String[] { "EchoServerV3Handler" };// 채널 파이프 2개 등록하여 사용하는 예제
		args = new String[] { "EchoServerV4Handler" };// 채널 파이프 2개 등록하지만, channelRead() 메서드 사용법 예제
		EchoServerV1.main(args);
	}
}

/**
 * [A-01] 클라이언트접속요청 > 서버소켓채널 > 소켓채널 > 채널 파이프라인 등록 >
 * ChannelInitializer.initChannel() 메서드 자동 호출 > 소켓채널 > 채널파이프라인(이벤트 핸들러 > 이벤트
 * 처리코드)
 */
class EchoServerV1 {
	public static void main(String... args) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {/* 01. 클라이언트 소켓 채널 파이프라인 설정 */
						@Override /* 리액터 패턴 */
						public void initChannel(SocketChannel ch) {/* 02.채널 파이프라인 설정 */
							ChannelPipeline p = ch.pipeline();/* 03. 연결된 클라이언트 소켓 채널 가져오기 */
							if ("EchoServerV2Handler".equals(args[0])) {
								p.addLast(new EchoServerV2Handler());
								/* 채널파이프를 여러개 등록 할 수 있다. */
							} else if ("EchoServerV3Handler".equals(args[0])) {
								p.addLast(new EchoServerV3FirstHandler());
								p.addLast(new EchoServerV3SecondHandler());
							} else if ("EchoServerV4Handler".equals(args[0])) {
								p.addLast(new EchoServerV4FirstHandler());
								p.addLast(new EchoServerV4SecondHandler());
							} else {
								p.addLast(new EchoServerV1Handler());/* 04. 채널 파이프라인 등록 */
							}
						}
					});
			ChannelFuture f = b.bind(8888).sync();/* 퓨처패턴 */
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

/**
 * [A-02-01] 데이터를 수신하자마다 상대방에게 데이터를 전송
 */
class EchoServerV1Handler extends ChannelInboundHandlerAdapter {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(EchoServerV1Handler.class);

	/** 이벤트 루프에 채널 등록(클라이언트가 connect() 수행될때) */
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info(">> 001. channelRegistered(ChannelHandlerContext ctx) call");
		ctx.fireChannelRegistered();
	}

	/**
	 * 채널 활성화(서버 또는 클라이언트가 상대방에 연결한 직후에 한 번 수행할 작업을 처리하기에 적합) 1. 서버 애플리케이션에 연결된
	 * 클라이언트의 연결 개수를 셀때 2. 서버 애플리케이션에 연결된 클라이언트에게 최초 연결에 대한 메시지 전송할 때 3. 클라이언트
	 * 애플리케이션이 연결된 서버에 최초 메시지를 전달할 때 4. 클라이언트 애플리케이션에서 서버에 연결된 상태에 대한 작업이 필요할 때
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info(">> 002. channelActive(ChannelHandlerContext ctx) call");
		ctx.fireChannelActive();
	}

	/** 데이터 수신 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.info(">> 003. channelRead(ChannelHandlerContext ctx, Object msg)");
		ByteBuf readMessage = (ByteBuf) msg;
		System.out.println("channelRead: " + readMessage.toString(Charset.defaultCharset()));
		ctx.writeAndFlush(msg);// 데이터를 수신하자마자 상대방에게 데이터를 전송
	}

	/** 채널 비활성화(이벤트가 발생한 이후에는 채널에 대한 입출력 작업을 수행할 수 없다.) */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info(">> 005. channelInactive(ChannelHandlerContext ctx) call");
		ctx.fireChannelInactive();
	}

	/** 이벤트 루프에서 채널 제거(채널에서 발생한 이벤트를 처리할 수 없다) */
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info(">> 006. channelUnregistered(ChannelHandlerContext ctx) call");
		ctx.fireChannelUnregistered();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

/**
 * [A-02-02] 소켓 채널에세 더 이상 읽어들일 데이터가 없을 때 데이터는 전송한다.
 */
class EchoServerV2Handler extends ChannelInboundHandlerAdapter {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(EchoServerV2Handler.class);

	/** 데이터 수신 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.info(">> 003. channelRead(ChannelHandlerContext ctx, Object msg)");
		ByteBuf readMessage = (ByteBuf) msg;
		System.out.println("channelRead: " + readMessage.toString(Charset.defaultCharset()));
		
		// ChannelHandlerContext를 통해서 네티 프레임워크에서 초기화된 ByteBufAllocator를 참조할 수 있다.
		// ByteBufAllocator는 바이트 버퍼 풀을 관리하는 인터페이스며 플랫폼의 지원 여부에 따라 다이렉트 버퍼와
		// 힙 버퍼 풀을 생성한다. 기본적으로 다이렉트 버퍼 풀을 생성하며 애플리케이션 개발자의 필요에
		// 따라 힙 버퍼 풀을 생성할 수도 있다.
		ByteBufAllocator byteBufAllocator = ctx.alloc();
		
		// ByteBufAllocator의 buffer 메서드를 사용하여 생성된 바이트 버퍼는 ByteBufAllocator의 풀에서
		// 관리되며 바이트 버퍼를 채널에 기록하거나 명시적으로 release 메서드를 호출하면 바이트 버퍼 풀로 돌아간다.
		ByteBuf newBuffer = byteBufAllocator.buffer();
		
		// write 메서드의 인수로 바이트 버퍼가 입력되면 데이터를 채널에 기록하고 난 뒤에 버퍼 풀로 돌아간다.
		ctx.write(msg);// 소켓 채널에세 더 이상 읽어들일 데이터가 없을 때 데이터는 전송한다.
	}

	/** 데이터 수신 완료 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info(">> 004. channelReadComplete(ChannelHandlerContext ctx) call");
		// ctx.fireChannelReadComplete();
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

class EchoServerV3FirstHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf readMessage = (ByteBuf) msg;
		System.out.println("channelRead: " + readMessage.toString(Charset.defaultCharset()));
		ctx.write(msg);
	}
}
class EchoServerV3SecondHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

class EchoServerV4FirstHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf readMessage = (ByteBuf) msg;
		System.out.println("FirstHandler channelRead: " + readMessage.toString(Charset.defaultCharset()));
		ctx.write(msg);
		ctx.fireChannelRead(msg);// 이 부분이 없으면 EchoServerV4SecondHandler.channelRead()가 수행되지 않는다.
	}
}
class EchoServerV4SecondHandler extends ChannelInboundHandlerAdapter {
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf readMessage = (ByteBuf) msg;
        System.out.println("SecondHandler channelRead : " + readMessage.toString(Charset.defaultCharset()));
    }
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
