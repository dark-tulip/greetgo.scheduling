package kz.greetgo.scheduling;

import java.util.*;

public class ExecutionPool {

  public int maxPoolSize = 10;

  public int maxThreadWaitingDelayInMillis = 60 * 1000;

  public String threadNamePrefix = "Scheduling-";

  private final List<Executor> executorList = new ArrayList<>();

  private final LinkedList<Task> queue = new LinkedList<>();

  public void runTask(Task task) {

    final boolean mayParallel = task.mayParallel();

    if (!mayParallel) for (Executor executor : executorList) {
      if (executor.working() && task.equals(executor.currentTask())) return;
    }

    for (Task queueTask : queue) {
      if (queueTask.equals(task)) return;
    }

    queue.addLast(task);
  }

  public void tryExecuteFromQueue() {
    for (int i = 0, n = executorList.size(); i < n && queue.size() > 0; i++) {
      Executor executor = executorList.get(i);
      if (!executor.working()) {
        executor.startExecution(queue.pollFirst());
      }
    }

    while (queue.size() > 0 && executorList.size() < maxPoolSize) {
      Executor executor = newExecutor();
      executorList.add(executor);
      executor.startExecution(queue.pollFirst());
    }
  }

  public void stopLongWaitingThreads() {
    final long now = System.currentTimeMillis();
    for (Executor executor : executorList) {
      if (executor.working()) continue;
      if (executor.hasWorkingThread()) {
        final long lastFinishMoment = executor.getLastFinishMoment();
        if (now - lastFinishMoment >= maxThreadWaitingDelayInMillis) {
          executor.stopThread();
        }
      }
    }
  }

  private Executor newExecutor() {
    int len = ("" + maxPoolSize).length();

    String nomer = "" + (executorList.size() + 1);

    while (nomer.length() < len) nomer = '0' + nomer;

    return new Executor(threadNamePrefix + nomer);
  }

  public void deactivate() {
    for (Executor executor : executorList) {
      executor.stopThread();
    }
  }

  public static Map<String, ExecutionPool> poolsForTasks(List<Task> tasks) {
    Set<String> poolNames = new HashSet<>();
    for (Task task : tasks) {
      poolNames.add(task.getPoolName());
    }

    {
      Map<String, ExecutionPool> ret = new HashMap<>();
      for (String poolName : poolNames) {
        ret.put(poolName, new ExecutionPool());
      }
      return ret;
    }
  }
}
