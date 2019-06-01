package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Scheduler {

  private final List<TaskHolder> taskHolderList;
  private final Map<String, ExecutionPool> executionPoolMap;
  private final long pingDelayMillis;

  Scheduler(List<TaskHolder> taskHolderList,
            Map<String, ExecutionPool> executionPoolMap, long pingDelayMillis) {
    this.taskHolderList = taskHolderList;
    this.executionPoolMap = executionPoolMap;
    this.pingDelayMillis = pingDelayMillis;
  }

  private final AtomicBoolean working = new AtomicBoolean(true);

  private long lastMillis;
  private long schedulerStartedAtMillis;

  private final AtomicReference<Thread> thread = new AtomicReference<>(null);

  public void startup() {

    schedulerStartedAtMillis = lastMillis = System.currentTimeMillis();

    Thread aThread = new Thread(() -> {

      while (working.get()) {

        tryRunTasks();

        if (pingDelayMillis > 0) {
          try {
            Thread.sleep(pingDelayMillis);
          } catch (InterruptedException e) {
            working.set(false);
          }
        }


      }

    });
    thread.set(aThread);

    aThread.start();

  }

  private void tryRunTasks() {

    long current = System.currentTimeMillis();

    for (TaskHolder taskHolder : taskHolderList) {

      Trigger trigger = taskHolder.task.trigger();

      if (taskHolder.runCount.get() > 0 && !trigger.isParallel()) {
        continue;
      }

      if (!trigger.isHit(schedulerStartedAtMillis, lastMillis, current)) {
        continue;
      }

      taskHolder.runCount.incrementAndGet();

      executionPoolMap
        .get(taskHolder.task.executionPoolName())
        .execute(taskHolder);

    }

    lastMillis = current;

  }

  public void shutdown() {
    working.set(false);
  }

}
