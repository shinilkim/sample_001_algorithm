package main.java.netty.telnet;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import com.github.nettybook.ch8.TelnetServerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//[Spring] JAR
//commons-logging-1.2.jar
//spring-core-4.3.1.8.RELEASE.jar 
//spring-context-4.3.1.8.RELEASE.jar 
//spring-beans-4.3.1.8.RELEASE.jar
//spring-aop-4.3.1.8.RELEASE.jar
//spring-expression-4.3.1.8.RELEASE.jar

// [telnet-server.properties]
//boss.thread.count=1
//worker.thread.count=10
//tcp.port=8888
public class NettyServer_006 {
	public static void main(String...args) {
		TelnetServerBySpring.main(args);
	}
}

/*
 * boss.thread.count=1 worker.thread.count=10 tcp.port=8023
 */
/* 01. 지정된 클래스가 스프링의 설정 정보를 포함한 클래스임을 표시
 * TelnetServerConfig 클래스에는 Bean 설정 정보와 ComponentScan, PropertySource 정보가 포함되어 있다.
 * */
@Configuration 
/* 02. ComponentScan 어노테이션은 스프링의 컨텍스트가 클래스를 동적으로 찾을 수 있도록 한다
 * 입력되는 패키지명을 포함한 하위 패키지를 대상으로 검색한다.
 * */
@ComponentScan("main.java.netty.telnet") 
/* 03. PropertySource 어노트에션은 설정 정보를 가진 파일의 위치에서 파일을 읽어서 Environment 객체로 자동 저장
 * 설정 파일의 이름은 telnet-server.properties 이며 클래스 패스에서 설정 파일을 검색
 * */
@PropertySource("classpath:config/telnet-server.properties") 
class TelnetServerConfig {
	@Value("${boss.thread.count}") /* 04. */
	private int bossCount;
	
	@Value("${worker.thread.count}") /* 05. */
	private int workerCount;
	
	@Value("${tcp.port}") /* 06. */
	private int tcpPort;

	public int getBossCount() {
		return bossCount;
	}

	public int getWorkerCount() {
		return workerCount;
	}

	public int getTcpPort() {
		return tcpPort;
	}
	
	/* 07. 설정 파일에서 읽어들인 tcp.port 정보로부터 InetSocketAddress 객체를 생성한다.
	 * 객체 이름을 tcpSocketAddress로 지정
	 * 스프링 컨텍스트에 tcpSocketAddress라는 이름으로 추가되면 다른 Bean에서 사용할 수 있다.
	 * */
	@Bean(name="tcpSocketAddress")
	public InetSocketAddress tcpPort() {
		return new InetSocketAddress(tcpPort);
	}
	
	/* 08. PropertySource 어노테이션에서 사용할 Environment 객체를 생성하는 Bean을 생성*/
	@Bean 
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}

/* 01. 클래스에 Component 어노테이션을 지정하여 TelnetServerV2 클래스가
 * 스프링 컨텍스트에 등록되게 한다. */
@Component
final class TelnetServerV2{
	/* 02. TelnetServerV2 클래스의 port 필드값이 자동 할당되도록 지정한다.*/
	@Autowired
	/* 03. 스프링 컨텍스트에 지정된 객체 이름 중에 tcpSocketAddress에 해당하는 객체를 할당하도록 지정*/
	@Qualifier("tcpSocketAddress")
	private InetSocketAddress address;
	
	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new TelnetServerInitializer());
			
			ChannelFuture future = b.bind(address).sync();
			future.channel().closeFuture().sync();
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}

class TelnetServerBySpring{
	public static void main(String...args) {
		/* 01. 어노테이션 기반의 스프링 컨텍스트 객체를 생성한다.*/
		AbstractApplicationContext springContext = null;
		try {
			/* 02. 어노테이션 설정을 가진 클래스를 지정.
			 * 여기에 입력되는 클래스는 스프링 컨텍스트를 생성하는데 필요한 설정 정보가 포함되어 있다.*/
			springContext = new AnnotationConfigApplicationContext(TelnetServerConfig.class);
			springContext.registerShutdownHook();
			/* 03. 스프링 컨텍스트에서 TelnetServerV2 클래스의 객체를 가져온다.*/
			TelnetServerV2 server = springContext.getBean(TelnetServerV2.class);
			/* 04. 텔넛서버의 start 메소드를 실행한다.*/
			server.start();
		} finally {
			if( springContext != null)
				springContext.close();
		}
	}
}
