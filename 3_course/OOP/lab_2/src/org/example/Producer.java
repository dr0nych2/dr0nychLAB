package org.example;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Producer implements Runnable {
  private long id;
  private BlockingQueue<String> sendQueue;
  private BlockingQueue<String> eventQueue;

  private Random random = new Random();

  public Producer(long id, BlockingQueue<String> sendQueue, BlockingQueue<String> eventQueue) {
    this.id = id;
    this.sendQueue = sendQueue;
    this.eventQueue = eventQueue;
  }

  @Override
  public void run() {
    try {
      while (true) {
        int sleepDuration = random.nextInt(1, 10);
        String product = String.format("Product №%d", id);
        String event = String.format("Producer №%d: I produced \"%s\". Gonna sleep for %d seconds...", id, product,
            sleepDuration);

        sendQueue.put(product);
        eventQueue.put(event);

        TimeUnit.SECONDS.sleep(sleepDuration);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
