package kz.greetgo.scheduling;

import java.util.*;

public class Scheduler {

  public long idleSleepTime = 200;

  private final Map<String, ExecutionPool> pools = new HashMap<>();
  private final Set<Task> tasks = new HashSet<>();

  private volatile boolean active = true;

  public Scheduler(Collection<Task> tasks, Map<String, ExecutionPool> pools) {
    if (tasks == null || tasks.size() == 0) throw new IllegalArgumentException("no tasks");
    this.tasks.addAll(tasks);
    if (pools != null) this.pools.putAll(pools);

    for (Task task : tasks) {
      final String poolName = task.getPoolName();
      if (poolName == null) throw new IllegalArgumentException("Task does not have a pool name " + task.infoForError());
      if (!this.pools.containsKey(poolName)) throw new NoPoolWithName(poolName, task);
    }
  }

  private long lastRunOfStopLongWaitingThreads = 0;

  private void makeScheduleStep() {

    for (Task task : tasks) {
      if (task.disabled()) continue;
      if (task.isItTimeToRun()) {
        final String poolName = task.getPoolName();
        pools.get(poolName).runTask(task);
      }
    }

    int minimalMaxThreadWaitingDelayInMillis = 0;

    for (ExecutionPool pool : pools.values()) {
      pool.tryExecuteFromQueue();
      if (pool.maxThreadWaitingDelayInMillis > 0) {
        if (minimalMaxThreadWaitingDelayInMillis == 0
            || minimalMaxThreadWaitingDelayInMillis < pool.maxThreadWaitingDelayInMillis) {
          minimalMaxThreadWaitingDelayInMillis = pool.maxThreadWaitingDelayInMillis;
        }
      }
    }

    if (minimalMaxThreadWaitingDelayInMillis == 0) return;

    if (lastRunOfStopLongWaitingThreads == 0) {
      lastRunOfStopLongWaitingThreads = System.currentTimeMillis();
    } else if (System.currentTimeMillis() - lastRunOfStopLongWaitingThreads > minimalMaxThreadWaitingDelayInMillis) {

      for (ExecutionPool pool : pools.values()) {
        pool.stopLongWaitingThreads();
      }

    }
  }


  private final Runnable runner = new Runnable() {
    @Override
    public void run() {

      for (Task task : tasks) {
        task.schedulerStarted();
      }

      while (active) {

        makeScheduleStep();

        synchronized (sync) {
          try {
            sync.wait(idleSleepTime);
          } catch (InterruptedException e) {
          }
        }

      }
    }
  };

  private boolean started = false;

  public void startup(String schedulerMainThreadName) {
    markAsStarted();

    final Thread thread = new Thread(runner);

    if (schedulerMainThreadName != null) {
      thread.setName(schedulerMainThreadName);
    }

    thread.start();
  }

  private void markAsStarted() {
    if (started) throw new IllegalStateException("Scheduler already started");
    started = true;
  }

  public void startup() {
    startup("MAIN_SCHEDULER");
  }

  private final Object sync = new Object();

  public void shutdown() {
    if (!active) return;
    active = false;
    synchronized (sync) {
      sync.notifyAll();
    }

    for (ExecutionPool pool : pools.values()) {
      pool.deactivate();
    }
  }

  public void startupInMyThread() {
    markAsStarted();
    runner.run();
  }
}
