package kz.greetgo.scheduling;

import java.util.List;
import java.util.Map;

public class FullSchedulerProbe {

  public static class SomeScheduler {

    @Scheduled("09:38")
    public void asd() {
      System.out.println("Hello from asd");
    }

  }

  public static void main(String[] args) {

    SomeScheduler ss = new SomeScheduler();

    TaskCollector taskCollector = new TaskCollector("build/scheduler");
    taskCollector.collect(ss);

    List<Task> tasks = taskCollector.getTasks();

    Map<String, ExecutionPool> executionPoolMap = ExecutionPool.poolsForTasks(tasks);

    Scheduler scheduler = new Scheduler(tasks, executionPoolMap);

    scheduler.startup();

    System.out.println("Started up");
  }
}
