package main.java.netty;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;

/**
 * 인증서 다운로드 : http://slproweb.com/products/Win32OpenSSL.html 기본설치 디렉토리 :
 * C:\OpenSSL-Win64\bin (PATH에 추가하자)
 * 
 * 1. 개인키(비밀키) 생성하기 명령어 : openssl genrsa -aes256 -out privatekey.pem 2048
 * privatekey.pem 개인키(비밀키)가 생성된다.
 * 
 * 2. 공개키 생성 openssl req -new -key privatekey.pem -out netty.csr - netty.csr 파일이
 * 생성되었다.
 * 
 * 3. 전사서명으로 인증서 만들기 openssl x509 -in netty.csr -out netty.crt -req -signkey
 * privatekey.pem -days 35600 - 10년짜리 인증서가 netty.crt 만들어졌다.
 * 
 * 4. 인증서와 개인키의 정합성 검증 openssl x509 -noout -text -in ./netty.crt
 * 
 * openssl rsa -noout -text -in privatekey.pem
 * 
 * 5. ANS.1 > PKCS#8 형식의 개인키 만들기 openssl pkcs8 -topk8 -inform PEM -outform PEM
 * -in privatekey.pem -out privatekey.pkcs8.pem
 */
public class NettyServer_005 {
	public static void main(String... args) throws Exception {
//		TelnetServer.main(args);// http 텔넷 서버
		HttpSnoopServer.main(args);// https 텔렛 서버
	}
}

/**
 * https 텔넷 서버
 *
 */
class HttpSnoopServer {
	private static final int PORT = 8443;/* 01. SSL/TLS 연결을 위한 포트(기본포트는 443) */

	public static void main(String... args) throws Exception {
		SslContext sslCtx = null;
		try {
			File certChainFile = new File(
					"C:\\home\\workspace\\java\\04-DataScience\\bin\\netty.crt");/* 02. 인증서 파일 지정 */
			File keyFile = new File(
					"C:\\home\\workspace\\java\\04-DataScience\\bin\\privatekey.pkcs8.pem");/* 03. 개인키 파일 지정 */
			if (keyFile.exists()) {
				sslCtx = SslContext.newServerContext(certChainFile, keyFile, "rhkr$dud$al$qh$wl$");/* 04. */
			} else {
				throw new FileNotFoundException();
			}

		} catch (SSLException e) {
			e.printStackTrace();
		}

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new HttpSnoopServerInitializer(sslCtx));/* 05.파이프라인 생성 */

			Channel ch = b.bind(PORT).sync().channel();
			ch.closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {
	private final SslContext sslCtx;

	public HttpSnoopServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpObjectAggregator(1048576));
		p.addLast(new HttpResponseEncoder());
		p.addLast(new HttpSnoopServerHandler());
	}
}

class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {
	private HttpRequest request;
	private final StringBuilder buf = new StringBuilder();

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest request = this.request = (HttpRequest) msg;

			if (HttpHeaders.is100ContinueExpected(request)) {
				send100Continue(ctx);
			}

			buf.setLength(0);
			buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
			buf.append("===================================\r\n");

			buf.append("VERSION: ").append(request.protocolVersion()).append("\r\n");
			buf.append("HOSTNAME: ").append(HttpHeaders.getHost(request, "unknown")).append("\r\n");
			buf.append("REQUEST_URI: ").append(request.uri()).append("\r\n\r\n");

			HttpHeaders headers = request.headers();
			if (!headers.isEmpty()) {
				for (Map.Entry<String, String> h : headers) {
					String key = h.getKey();
					String value = h.getValue();
					buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
				}
				buf.append("\r\n");
			}

			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
			Map<String, List<String>> params = queryStringDecoder.parameters();
			if (!params.isEmpty()) {
				for (Entry<String, List<String>> p : params.entrySet()) {
					String key = p.getKey();
					List<String> vals = p.getValue();
					for (String val : vals) {
						buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
					}
				}
				buf.append("\r\n");
			}
			appendDecoderResult(buf, request);
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;

			ByteBuf content = httpContent.content();
			if (content.isReadable()) {
				buf.append("CONTENT: ");
				buf.append(content.toString(CharsetUtil.UTF_8));
				buf.append("\r\n");
				appendDecoderResult(buf, request);
			}
			
			if(msg instanceof LastHttpContent) {
				buf.append("END OF CONTENT\r\n");
				
				LastHttpContent trailer = (LastHttpContent) msg;
				if(!trailer.trailingHeaders().isEmpty()) {
					buf.append("\r\n");
					for(String name: trailer.trailingHeaders().names()) {
						for (String value: trailer.trailingHeaders().getAll(name)) {
							buf.append("TRAILING HEADER: ");
							buf.append(name).append(" = ").append(value).append("\r\n");
						}
					}
					buf.append("\r\n");
				}
				
				if(!writeResponse(trailer, ctx)) {
					ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
				}
			}
		}
	}
	
	private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
		DecoderResult result = o.decoderResult();
		if(result.isSuccess()) {
			return;
		}
		
		buf.append(".. WITH DECODER FAILURE: ");
		buf.append(result.cause());
		buf.append("\r\n");
	}
	
	private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		FullHttpResponse response = new DefaultFullHttpResponse(
				HTTP_1_1, currentObj.decoderResult().isSuccess() ? OK : BAD_REQUEST,
						Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
		
		response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
		
		if(keepAlive) {
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		
		String cookieString = request.headers().get(COOKIE);
		if(cookieString != null) {
			Set<Cookie> cookies = CookieDecoder.decode(cookieString);
			if(!cookies.isEmpty()) {
				for(Cookie cookie: cookies) {
					response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookies));
				}
			}
		} else {
			response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
			response.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
		}
		
		ctx.write(response);
		return keepAlive;
	}
	
	private static void send100Continue(ChannelHandlerContext ctx) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.CONTINUE);
		ctx.write(response);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

/**
 * 
 * http 텔넷 서버
 *
 */
class TelnetServer {
	private static final int PORT = 8889;// 텔넷 서버의 서비스에 사용할 포트지정

	public static void main(String... args) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new TelnetServerInitializer());// 텔넷서버의 채널
																											// 파이프라인 설정

			ChannelFuture future = b.bind(PORT).sync();
			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

/**
 * 
 * 텔넷 서버 > 파이프라인
 *
 */
class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {
	private static final StringDecoder DECODER = new StringDecoder();// 모든 채널 파이프라인에서 공유
	private static final StringEncoder ENCODER = new StringEncoder();

	private static final TelnetServerHandler SERVER_HANDLER = new TelnetServerHandler();

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		// 기본 디코더로서 구분자 기반의 패킷을 처리
		pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

		pipeline.addLast(DECODER);
		pipeline.addLast(ENCODER);
		pipeline.addLast(SERVER_HANDLER);
	}
}

/**
 * 
 * 텔넷 서버 > 파이프라인 > 핸들러
 *
 */
@Sharable /* 01. 다중 스레드기반에서 스레드 경합 없이 채널 파이프라인 공유 */
class TelnetServerHandler extends SimpleChannelInboundHandler<String> {/* 02. */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {/* 03. 채널이 생성되면 호출 */
		// Send greeting for a new connection
		ctx.write("환영합니다. " + InetAddress.getLocalHost().getHostName() + "에 접속하였습니다!\r\n");/* 04. 환영인사 */
		ctx.write("현재 시간은 " + new Date() + " 입니다.\r\n");
		ctx.flush();/* 05. 채널에 기록된 데이터를 즉시 클라이언트로 전송한다. */
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
		String response;
		boolean close = false;

		if (request.isEmpty()) {/* 06. 클라이언트가 명령입력 없이 엔터 */
			response = "명령을 입력해 주세요.\r\n";
		} else if ("bye".equals(request.toLowerCase())) {/* 07. */
			response = "좋은 하루 되세요!\r\n";
			close = true;
		} else {/* 08. */
			response = "입력하신 명령이 '" + request + "' 입니까?\r\n";
		}
		/* 09. 입력한 명령행을 채널에 기록 */
		ChannelFuture future = ctx.write(response);
		/* 10. 비동기로 채널닫기 */
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();/* 11. 클라이언트로 데이터 전송 */
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
