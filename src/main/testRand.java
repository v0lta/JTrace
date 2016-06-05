package main;

import java.util.Random;

public class testRand {

	public static void main(String[] args) {
		Random rnd = new Random();
		for (int i = 0; i < 1000; i++){
			System.out.println(-1.0 + 2.0 * rnd.nextDouble());
		}

	}

}
