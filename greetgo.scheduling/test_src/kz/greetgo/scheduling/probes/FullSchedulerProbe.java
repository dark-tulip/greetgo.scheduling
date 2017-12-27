package kz.greetgo.scheduling.probes;

import kz.greetgo.scheduling.ExecutionPool;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.Scheduler;
import kz.greetgo.scheduling.Task;
import kz.greetgo.scheduling.TaskCollector;
import kz.greetgo.scheduling.ThrowableCatcher;

import java.util.List;
import java.util.Map;

public class FullSchedulerProbe {

  public static class SomeScheduler {

    @Scheduled("09:47")
    public void asd() {
      System.out.println("Hello from asd");
      throw new RuntimeException("asd");
    }

  }

  public static void main(String[] args) {

    SomeScheduler ss = new SomeScheduler();

    TaskCollector taskCollector = new TaskCollector("build/scheduler");
    taskCollector.throwableCatcher = new ThrowableCatcher() {
      @Override
      public void catchThrowable(Throwable throwable) {
        System.out.println("Wow " + throwable);
      }
    };
    taskCollector.collect(ss);

    List<Task> tasks = taskCollector.getTasks();

    Map<String, ExecutionPool> executionPoolMap = ExecutionPool.poolsForTasks(tasks);

    Scheduler scheduler = new Scheduler(tasks, executionPoolMap);

    scheduler.startup();

    System.out.println("Started up");
  }
}
