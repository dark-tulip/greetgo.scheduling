package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.collector.Task;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskHolder {

  public final Task task;
  public final ThrowCatcher throwCatcher;

  public TaskHolder(Task task, ThrowCatcher throwCatcher) {
    this.task = task;
    this.throwCatcher = throwCatcher;
  }

  public final AtomicInteger runCount = new AtomicInteger(0);

}
