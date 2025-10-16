package org.example;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
  private long id;
  private BlockingQueue<String> receiveQueue;
  private BlockingQueue<String> eventQueue;

  public Consumer(long id, BlockingQueue<String> receiveQueue, BlockingQueue<String> eventQueue) {
    this.id = id;
    this.receiveQueue = receiveQueue;
    this.eventQueue = eventQueue;
  }

  @Override
  public void run() {
    try {
      while (true) {
        String product = receiveQueue.take();
        String event = String.format("Consumer №%d: I got \"%s\".", id, product);

        eventQueue.put(event);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
