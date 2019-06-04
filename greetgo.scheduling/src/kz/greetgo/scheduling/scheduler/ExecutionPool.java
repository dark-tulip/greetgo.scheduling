package kz.greetgo.scheduling.scheduler;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutionPool {
  public static final String DEF_POOL_NAME = "default";

  private final String poolName;
  private final int maxSize;

  public ExecutionPool(String poolName, int maxSize) {
    this.poolName = poolName;
    this.maxSize = maxSize;
  }

  private final AtomicInteger threadSize = new AtomicInteger(0);

  private final ConcurrentLinkedQueue<TaskHolder> queue = new ConcurrentLinkedQueue<>();

  public void execute(TaskHolder taskHolder) {

    queue.add(taskHolder);

    while (true) {
      int size = threadSize.get();
      int newSize = size + 1;

      if (newSize > maxSize) {
        return;
      }

      if (threadSize.compareAndSet(size, newSize)) {
        runInNewThread();
        return;
      }

    }

  }

  private void runInNewThread() {

    new Thread(() -> {
      try {

        while (true) {
          TaskHolder taskHolder = queue.poll();
          if (taskHolder == null) {
            break;
          }
          executeTaskNowAndHere(taskHolder);
        }


      } finally {
        threadSize.decrementAndGet();
      }

    }, newThreadName()).start();

  }

  private final AtomicInteger nextThreadIndex = new AtomicInteger(1);

  private String newThreadName() {
    return "pool-" + poolName + "-" + nextThreadIndex.getAndIncrement();
  }

  private void executeTaskNowAndHere(TaskHolder taskHolder) {

    try {

      try {
        taskHolder.task.job().execute();
      } catch (Throwable throwable) {
        taskHolder.throwCatcher.catchThrowable(throwable);
      }

    } finally {
      taskHolder.runCount.decrementAndGet();
    }

  }

}
