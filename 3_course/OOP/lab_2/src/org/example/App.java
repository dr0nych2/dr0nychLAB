package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class App {
  private static long nThreads = 0;

  public static void main(String[] args) {
    try {
      nThreads = Integer.parseUnsignedInt(args[0]);
    } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
      System.err.println("Incorrect Input: argument should be unsigned integer");
      return;
    }

    BlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();

    new Thread(() -> {
      ExecutorService producers = Executors.newCachedThreadPool();
      ExecutorService consumers = Executors.newCachedThreadPool();
      try (producers; consumers) {
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
        for (long i = 0; i < nThreads; i++) {
          producers.execute(new Producer(i + 1, messageQueue, eventQueue));
          consumers.execute(new Consumer(i + 1, messageQueue, eventQueue));
        }
      }
    }).start();

    new Thread(() -> {
      try {
        while (true) {
          System.out.println(eventQueue.take());
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }).start();
  }
}
