package kz.greetgo.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ExecutionPool {

  public int maxPoolSize = 10;

  public int maxThreadWaitingDelayInMillis = 60 * 1000;

  public String threadNamePrefix = "Scheduling-";

  private final List<Executor> executorList = new ArrayList<>();

  private final ConcurrentLinkedQueue<Task> queue = new ConcurrentLinkedQueue<>();

  public void runTask(Task task) {
    queue.add(task);
    task.taskRunStatus.markStarted();
    task.markThatInExecutionQueue();
  }

  public void tryExecuteFromQueue() {
    for (int i = 0, n = executorList.size(); i < n && queue.size() > 0; i++) {
      Executor executor = executorList.get(i);
      if (!executor.working()) {
        executor.startExecution(queue.poll());
      }
    }

    while (executorList.size() < maxPoolSize) {
      Task task = queue.poll();
      if (task == null) break;
      Executor executor = newExecutor();
      executorList.add(executor);
      executor.startExecution(task);
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

    StringBuilder number = new StringBuilder(len);
    number.append(executorList.size() + 1);

    while (number.length() < len) number.insert(0, '0');

    return new Executor(threadNamePrefix + number);
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
