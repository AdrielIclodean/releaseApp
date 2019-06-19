package com.ibm.release;

import java.util.concurrent.CountDownLatch;

public class Test {

	static CountDownLatch startCountDownLatch = new CountDownLatch(3);
	static CountDownLatch enDownLatch = new CountDownLatch(5);

	public static void main(String[] args) throws InterruptedException {

		new Thread(new Test.MyRunnable(startCountDownLatch, enDownLatch, "Adriel")).start();
		new Thread(new Test.MyRunnable(startCountDownLatch, enDownLatch, "Cadmiel")).start();
		new Thread(new Test.MyRunnable(startCountDownLatch, enDownLatch, "Damaris")).start();
		new Thread(new Test.MyRunnable(startCountDownLatch, enDownLatch, "Ana")).start();
		new Thread(new Test.MyRunnable(startCountDownLatch, enDownLatch, "Mimi")).start();

		System.out.println("All Set");
		System.out.println("3");
		startCountDownLatch.countDown();
		System.out.println("2");
		startCountDownLatch.countDown();
		System.out.println("1");
		startCountDownLatch.countDown();

		System.out.println("Go");

		enDownLatch.await();
		System.out.println("Finished");

	}

	private static class MyRunnable implements Runnable {

		CountDownLatch start;
		CountDownLatch end;
		private String name;

		public MyRunnable(CountDownLatch start, CountDownLatch end, String name) {
			super();
			this.start = start;
			this.end = end;
			this.name = name;

		}

		@Override
		public void run() {

			try {
				System.out.println(name + " is prepared");
				start.await();

				System.out
						.println("Participant  " + name + " arrived at destination ");

				end.countDown();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
