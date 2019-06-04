package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.collector.SchedulerConfigStore;
import kz.greetgo.scheduling.collector.SchedulerConfigStoreInFile;
import kz.greetgo.scheduling.collector.Task;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static kz.greetgo.scheduling.collector.TaskCollector.newTaskCollector;
import static kz.greetgo.scheduling.scheduler.SchedulerBuilder.newSchedulerBuilder;

public class SchedulerTest {

  public static class TestSchedulerController {

    @FromConfig("ping repeater")
    @Scheduled("repeat every 5 sec")
    public void ping() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("ping " + sdf.format(new Date()));
    }

    @FromConfig("Симулятор летает в небесах")
    @Scheduled("repeat every 3 sec")
    public void pingWow() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("ping wow " + sdf.format(new Date()));
    }

    @FromConfig("Генерирует ошибку")
    @Scheduled("# 11:11:11")
    public void error() {
      throw new RuntimeException("ERROR WOW");
    }

  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  public void testScheduler() throws InterruptedException, IOException {

    Path configRoot = Paths.get("build/scheduler_config_dir");
    SchedulerConfigStore configStore = new SchedulerConfigStoreInFile(configRoot);

    TestSchedulerController controller = new TestSchedulerController();

    List<Task> tasks = newTaskCollector()
      .setSchedulerConfigStore(configStore)
      .setConfigExtension(".cfg.txt")
      .setConfigErrorsExtension(".cfg.error.txt")
      .addController(controller)
      .getTasks();

    Scheduler scheduler = newSchedulerBuilder()
      .setPingDelayMillis(500)
      .setDefaultExecutionPoolSize(100)
      .setExecutionPoolSize("large", 1000)
      .setThrowCatcher((throwable -> {
        System.out.println("QQQ START");
        throwable.printStackTrace(System.out);
        System.out.println("QQQ END");
      }))
      .addTasks(tasks)
      .build();

    scheduler.startup();

    File stop1 = configRoot.resolve("stop1").toFile();
    File stop2 = configRoot.resolve("stop2").toFile();
    File freezeTemplate = configRoot.resolve("freeze-template").toFile();
    File freeze = configRoot.resolve("freeze").toFile();
    if (!freeze.exists()) {
      freezeTemplate.createNewFile();
    }

    stop1.createNewFile();

    while (stop1.exists() && freeze.exists()) {
      Thread.sleep(1000);
    }

    scheduler.shutdown();

    stop2.createNewFile();

    while (stop2.exists() && freeze.exists()) {
      Thread.sleep(1000);
    }

    System.out.println("The End");

    stop1.delete();
    stop2.delete();
  }

}
