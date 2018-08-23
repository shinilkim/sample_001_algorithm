package main.java.netty;

import io.netty.util.concurrent.Future;

public class NettyServer_003 {
	public static void main(String...args) throws Exception{
		SpecialCake.main(args);
	}
}

class SpecialCake {

	public static void main(String... args) throws InterruptedException {
		Bakery bakery = new Bakery();

		Future future = bakery.orderCake();

		doSomething();

		if (future.isDone()) {
			Cake cake = new Cake("cake1");
		} else {
			while (!future.isDone()) {
				doSomething();
			}

			Cake cake = new Cake("cake2");
		}

	}

	private static void doSomething() throws InterruptedException {
		Thread.sleep(100);
	}
}

class Bakery {
	public Future orderCake() {
		return null;
	}
}

class Cake {
	private String name;

	public Cake(String name) {
		this.name = name;
	}
}
