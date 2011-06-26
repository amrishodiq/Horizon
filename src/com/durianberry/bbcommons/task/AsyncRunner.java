package com.durianberry.bbcommons.task;

public class AsyncRunner extends Thread {
  private final Task task;

  public AsyncRunner(final Task task) {
    this.task = task;
  }

  public void run() {
    task.execute();
  }
}
