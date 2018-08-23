package main.java.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class SocketServer_001 {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(SocketServer_001.class);
	public static void main(String... args) throws Exception {
		logger.info("Logger Test");
		// BlockingServer.main(args);
		NonBlockingServer.main(args);
	}
}

/**
 * BlockingServer
 */
class BlockingServer {
	public static void main(String... args) throws Exception {
		BlockingServer server = new BlockingServer();
		server.run();
		
	}

	private void run() throws IOException {
		ServerSocket server = new ServerSocket(8888);
		System.out.println("BlockServer 접속 대기중");

		while (true) {
			Socket sock = server.accept();
			System.out.println("클라이언트 연결됨.");

			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();

			while (true) {
				try {
					int request = in.read();
					out.write(request);
				} catch (IOException e) {
					break;
				}
			}
			sock.close();
		}
	}
}

/**
 * Non Blocking Server
 */
class NonBlockingServer {
	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

	private void run() {
		/* 01. 소괄호안에서 선언된 자원을 자동을 해제*/
		try (
				/* 02. 자신에게 등록된 채널에 변경 사힝이 발생했는지 검사하고 변경 사항이 발생한 채널에 대한 접근을 가능하게 해준다.*/
				Selector selector = Selector.open();
				/* 03. 블로킹 소켓과 다르게 소켓 채널을 먼저 생성하고 사용할 포트를 바인딩 */
				ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			) {
			if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {/* 04. Selector & ServerSocketChannel 객체 정상유무 확인*/
				serverSocketChannel.configureBlocking(false);/* 05. 블로킹 모드를 논블로킹 모드로 설정*/
				serverSocketChannel.bind(new InetSocketAddress(8888)); /* 06. 연결포트 설정*/

				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);/*07. Selector 객체에 등록*/
				System.out.println("NonBlockingServer 접속 대기중");

				while (true) {
					selector.select();/*08. Selector에 등록된 채널 변경사항 체크*/
					/*09. Selector  등록채널중에서 I/O 이벤트 발생목록 조회*/
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

					while (keys.hasNext()) {
						SelectionKey key = (SelectionKey) keys.next();
						/*10. I/O이벤트가 발생한 채널에서 등록한 이벤트가 감지되는 것을 방지하기 위하여 조회된 목록에서 제거*/
						keys.remove();

						if (!key.isValid()) {
							continue;
						}
						if (key.isAcceptable()) {/*11. 조회된 I/O 이벤트 : 연결 요청*/
							this.acceptOP(key, selector);
						} else if (key.isReadable()) {/*12. 조회된 I/O 이벤트 : 데이터 수신*/
							this.readOP(key);
						} else if (key.isWritable()) {/*13. 조회된 I/O 이벤트 : 데이터 쓰기*/
							this.writeOP(key);
						}
					}
				}
			} else {
				System.out.println("서버 소켓을 생성하지 못했습니다.");
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	private void acceptOP(SelectionKey key, Selector selector) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();/*14. 발생한 채널을 캐스팅*/
		SocketChannel socketChannel = serverChannel.accept();/*15. 연결을 수락하고 연결된 소켓 채널을 가져온다.*/
		socketChannel.configureBlocking(false);/*16.연결된 클라이언트 소켓 채널을 논블로킹 모드로 설정*/
		System.out.println("클라이언트 연결됨2");

		keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
		socketChannel.register(selector, SelectionKey.OP_READ);/*17.클라이언트 소켓 채널을 selector에 등록하여 I/O 이벤트 감시*/
	}

	private void readOP(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			buffer.clear();
			int numRead = -1;
			try {
				numRead = socketChannel.read(buffer);
			} catch (IOException e) {
				System.err.println("데이터 읽기 에러");
			}
			if (numRead == -1) {
				this.keepDataTrack.remove(socketChannel);
				System.out.println("클라이언트 연결 종료 : " + socketChannel.getRemoteAddress());
				socketChannel.close();
				key.cancel();
				return;
			}

			byte[] data = new byte[numRead];
			System.arraycopy(buffer.array(), 0, data, 0, numRead);
			System.out.println(new String(data, "UTF-8") + " from " + socketChannel.getRemoteAddress());
			doEchoJob(key, data);
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	private void writeOP(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		Iterator<byte[]> its = channelData.iterator();

		while (its.hasNext()) {
			byte[] it = its.next();
			its.remove();
			socketChannel.write(ByteBuffer.wrap(it));
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	private void doEchoJob(SelectionKey key, byte[] data) {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		List<byte[]> channelData = keepDataTrack.get(socketChannel);
		channelData.add(data);
		key.interestOps(SelectionKey.OP_WRITE);
	}

	public static void main(String... args) throws Exception {
		NonBlockingServer server = new NonBlockingServer();
		server.run();
	}
}
