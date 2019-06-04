package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.collector.SchedulerConfigStore;
import kz.greetgo.scheduling.collector.SchedulerConfigStoreInFile;
import kz.greetgo.scheduling.collector.Task;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static kz.greetgo.scheduling.collector.TaskCollector.newTaskCollector;
import static kz.greetgo.scheduling.scheduler.SchedulerBuilder.newSchedulerBuilder;

public class SchedulerExample {

  public static class ExampleSchedulerController {

    @FromConfig("This is description of this task")
    @Scheduled("repeat every 1 sec after pause in 3 sec")
    public void pingTask() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("ping " + sdf.format(new Date()));
    }

  }

  public static void main(String[] args) throws InterruptedException {

    Path configRoot = Paths.get("build/example_scheduler_config_dir");

    SchedulerConfigStore configStore = new SchedulerConfigStoreInFile(configRoot);

    ExampleSchedulerController controller = new ExampleSchedulerController();

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
        System.out.println("Error in scheduler method come here");
        throwable.printStackTrace(System.out);
      }))
      .addTasks(tasks)
      .build();

    scheduler.startup();

    Thread.sleep(10 * 1000);

    scheduler.shutdown();

    Thread.sleep(10 * 1000);
  }
}
