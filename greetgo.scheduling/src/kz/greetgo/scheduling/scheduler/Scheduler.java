package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Scheduler {

  private final List<TaskHolder> taskHolderList;

  // TODO это поле показывать в SchedulerStateInfo (пройтись по values)
  private final Map<String, ExecutionPool> executionPoolMap;
  private final long pingDelayMillis;

  Scheduler(List<TaskHolder> taskHolderList,
            Map<String, ExecutionPool> executionPoolMap, long pingDelayMillis) {
    this.taskHolderList = taskHolderList;
    this.executionPoolMap = executionPoolMap;
    this.pingDelayMillis = pingDelayMillis;
  }

  // TODO это поле показывать в SchedulerStateInfo
  private final AtomicBoolean working = new AtomicBoolean(true);

  // TODO это поле показывать в SchedulerStateInfo
  private long lastMillis;
  // TODO это поле показывать в SchedulerStateInfo
  private long schedulerStartedAtMillis;

  // TODO это поле показывать в SchedulerStateInfo
  private final AtomicLong workingCounter = new AtomicLong(0);
  // TODO это поле показывать в SchedulerStateInfo
  private final AtomicLong runTaskCounter = new AtomicLong(0);

  // TODO это поле показывать в SchedulerStateInfo
  private final AtomicBoolean wasInterrupted = new AtomicBoolean(false);

  private Thread mainSchedulerThread;

  public void startup() {

    schedulerStartedAtMillis = System.currentTimeMillis();
    lastMillis = schedulerStartedAtMillis - 1000L;

    mainSchedulerThread = new Thread(() -> {

      while (working.get()) {

        tryRunTasks();

        if (pingDelayMillis > 0) {
          try {
            Thread.sleep(pingDelayMillis);
          } catch (InterruptedException e) {
            working.set(false);
            wasInterrupted.set(true);
          }
        }

        workingCounter.incrementAndGet();

      }

    });

    mainSchedulerThread.setName("greetgo! Scheduler Thread");
    mainSchedulerThread.start();

  }

  private void tryRunTasks() {

    long current = System.currentTimeMillis();

    //TODO показать также все запущенные задачи и сколько раз они запущены и в каком пуле
    for (TaskHolder taskHolder : taskHolderList) {

      Trigger trigger = taskHolder.task.trigger();

      if (taskHolder.runCount.get() > 0 && !trigger.isParallel()) {
        continue;
      }

      if (!trigger.isHit(schedulerStartedAtMillis, lastMillis, current)) {
        continue;
      }

      taskHolder.runCount.incrementAndGet();

      //TODO собрать информацию по каждому пулу
      executionPoolMap
        .get(taskHolder.task.executionPoolName())
        .execute(taskHolder);

      runTaskCounter.incrementAndGet();

    }

    lastMillis = current + 1;

  }

  public void shutdown() {
    working.set(false);
  }

}
