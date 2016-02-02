package kz.greetgo.scheduling;

import kz.greetgo.scheduling.annotations.FromConfig;
import kz.greetgo.scheduling.annotations.Scheduled;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

public class TaskCollectorTest {

  @SuppressWarnings("unused")
  public static class C1 {
    @Scheduled("13:00")
    public void hello() {
    }

    @Scheduled("14:00")
    public void by() {
    }
  }

  @SuppressWarnings("unused")
  public static class C2 {
    @Scheduled("13:00")
    @FromConfig("Описание 1")
    public void helloWorld() {
    }

    @Scheduled("14:00")
    @FromConfig("Описание 2")
    public void goodByWorld() {
    }
  }

  @Test
  public void collect() throws Exception {
    String dir = "build/TaskCollectorTest/configs_" + RND.intStr(5);

    TaskCollector taskCollector = new TaskCollector(dir);

    C1 c1 = new C1();
    taskCollector.collect(c1);


    assertThat(taskCollector.getTasks()).hasSize(2);

    taskCollector.getTasks().get(0).isItTimeToRun();
    taskCollector.getTasks().get(1).isItTimeToRun();

    assertThat(new File(dir)).doesNotExist();

    C2 c2 = new C2();
    taskCollector.collect(c2);

    assertThat(taskCollector.getTasks()).hasSize(4);

    taskCollector.getTasks().get(2).isItTimeToRun();
    taskCollector.getTasks().get(3).isItTimeToRun();

    assertThat(new File(dir)).exists();
  }
}