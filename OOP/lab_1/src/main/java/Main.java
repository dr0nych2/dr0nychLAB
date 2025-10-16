package main.java;

import main.java.abstractProgram.AbstractProgram;
import main.java.abstractProgram.AbstractProgram.State;
import java.time.Duration;

public class Main {
  public static void main(String... args) {
    AbstractProgram program = new AbstractProgram(Duration.ofSeconds(1));

    Thread supervisor = new Thread(() -> {
      program.start();
      synchronized (program) {
        try {
          while (program.isAlive()) {
            program.wait();

            State state = program.getState();
            System.out.printf("Main: %s\n", state);
            switch (state) {
              case STOPPING:
                program.restart();
                System.out.println("Main: Program was Restarted!");
                break;
              case FATAL_ERROR:
                program.stop();
                System.out.println("Main: Program was Terminated!");
                break;
              default:
                break;
            }

            program.notifyAll();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.err.println(e);
        }
      }
    });

    try {
      supervisor.start();
      supervisor.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println(e);
    }
  }
}
