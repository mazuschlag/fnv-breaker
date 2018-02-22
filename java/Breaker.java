// Breaker.java
import java.io.*;

public class Breaker {
	private String fileName;
	public Breaker(String arg) {
		fileName = arg;
	}

	public void output() {
		System.out.print(fileName);
	}

	public static void main(String[] args) {
		Breaker breaker = new Breaker(args[0]);
		breaker.output();
	}
}