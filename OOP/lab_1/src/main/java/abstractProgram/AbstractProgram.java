package main.java.abstractProgram;

import java.util.Arrays;
import java.util.Random;
import java.lang.Thread;
import java.time.Duration;

public class AbstractProgram {
  public static enum State {
    UNKNOWN,
    STOPPING,
    RUNNING,
    FATAL_ERROR;
  }

  private State currentState = State.UNKNOWN;
  private Thread daemonThread;
  private boolean isAlive = false;
  private static final State[] states = Arrays.stream(State.values())
      .filter(state -> (state != State.UNKNOWN))
      .toArray(State[]::new);
  private static final int statesAmount = states.length;
  private static final Random randomizer = new Random();

  public AbstractProgram(Duration interval) {
    daemonThread = new Thread(() -> changeState(interval));
    daemonThread.setDaemon(true);
  }

  private synchronized void changeState(Duration interval) {
    try {
      while (true) {
        notifyAll();
        wait();

        Thread.sleep(interval);
        currentState = getRandomState();
        if (currentState == State.STOPPING) {
          System.out.println("Program: Stopping...");
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static State getRandomState() {
    return states[randomizer.nextInt(statesAmount)];
  }

  public void start() {
    daemonThread.start();
    isAlive = true;
  }

  public void restart() {
    currentState = State.RUNNING;
  }

  public void stop() {
    daemonThread.interrupt();
    isAlive = false;
  }

  public boolean isAlive() {
    return isAlive;
  }

  public State getState() {
    return currentState;
  }
}
