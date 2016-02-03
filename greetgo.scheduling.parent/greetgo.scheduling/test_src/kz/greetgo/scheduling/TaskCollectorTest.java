package kz.greetgo.scheduling;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.synchronizedList;
import static kz.greetgo.scheduling.ExecutionPool.poolsForTasks;
import static org.fest.assertions.api.Assertions.assertThat;

public class TaskCollectorTest {

  @SuppressWarnings("unused")
  public static class C1 implements HasScheduled {

    long createdAt = System.currentTimeMillis();

    final List<String> runs = synchronizedList(new ArrayList<String>());

    //@Scheduled("Повторять каждые 4 секунды, начиная с паузы 1.5 секунды")
    @Scheduled("Параллельно повторять каждые 10 секунд, начиная с паузы 7 секунд")
    public void hello() {
      long now = System.currentTimeMillis() - createdAt;
      String message = now + " hello 10/5";
      runs.add(message);
      System.out.println(message);
    }

    @Scheduled("Параллельно повторять каждые 20 секунд, начиная с паузы 10 секунду")
    public void by() {
      long now = System.currentTimeMillis() - createdAt;
      String message = now + " by 20/10";
      runs.add(message);
      System.out.println(message);
    }
  }

  @SuppressWarnings("unused")
  public static class C2 {
    @Scheduled("13:00")
    @FromConfig("Описание 1")
    public void helloWorld() {
    }

    @Scheduled("повторять каждые 13 мин, начиная с паузы 17 сек")
    @FromConfig("Описание 2 goodByWorld")
    public void goodByWorld() {
    }
  }

  @Test(enabled = false)
  public void collect_and_run() throws Exception {
    String dir = "build/TaskCollectorTest/collect_and_run_" + RND.intStr(5);

    TaskCollector taskCollector = new TaskCollector(dir);

    C1 c1 = new C1();
    taskCollector.collect(c1);
    C2 c2 = new C2();
    taskCollector.collect(c2);

    Scheduler scheduler = new Scheduler(taskCollector.getTasks(), poolsForTasks(taskCollector.getTasks()));
    scheduler.idleSleepTime = 2;

    scheduler.start("TASK_COLLECTOR_TEST");

    //Thread.sleep(16 * 1000 + 500);
    for (int i = 1; i <= 12; i++) {
      System.out.println("--------------- ping i = " + i);
      Thread.sleep(5 * 1000);
    }

    scheduler.shutdown();

    Thread.sleep(1000);

    System.out.println("--- ! ---   --- ! ---  OK  --- ! ---   --- ! ---");

    for (String run : c1.runs) {
      System.out.println(run);
    }

    System.out.println("COMPLETE");
  }

  private void startScheduler(List<Task> tasks) {
    for (Task task : tasks) {
      task.schdulerStarted();
    }
  }

  @Test
  public void collect() throws Exception {
    String dir = "build/TaskCollectorTest/collect_" + RND.intStr(5);

    TaskCollector taskCollector = new TaskCollector(dir);

    C1 c1 = new C1();
    taskCollector.collect(c1);


    assertThat(taskCollector.getTasks()).hasSize(2);

    startScheduler(taskCollector.getTasks());

    taskCollector.getTasks().get(0).isItTimeToRun();
    taskCollector.getTasks().get(1).isItTimeToRun();

    assertThat(new File(dir)).doesNotExist();

    C2 c2 = new C2();
    taskCollector.collect(c2);

    assertThat(taskCollector.getTasks()).hasSize(4);

    startScheduler(taskCollector.getTasks());

    taskCollector.getTasks().get(2).isItTimeToRun();
    taskCollector.getTasks().get(3).isItTimeToRun();

    assertThat(new File(dir)).exists();
  }

}