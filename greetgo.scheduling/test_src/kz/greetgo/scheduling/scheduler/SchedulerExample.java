package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.collector.SchedulerConfigStore;
import kz.greetgo.scheduling.collector.SchedulerConfigStoreInFile;
import kz.greetgo.scheduling.collector.Task;
import kz.greetgo.util.fui.FUI;

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
    @Scheduled("repeat every 1 sec after pause in 5 sec * 22 февраля")
    public void pingTask() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      System.out.println("ping " + sdf.format(new Date()));
    }

  }

  public static void main(String[] args) throws InterruptedException {

    Path root       = Paths.get("build/example_scheduler");
    Path configRoot = root.resolve("config_dir");

    FUI fui = new FUI(root);

    SchedulerConfigStore configStore = new SchedulerConfigStoreInFile(configRoot);

    ExampleSchedulerController controller = new ExampleSchedulerController();

    List<Task> tasks = newTaskCollector().setSchedulerConfigStore(configStore)
                                         .setConfigExtension(".cfg.txt")
                                         .setConfigErrorsExtension(".cfg.error.txt")
                                         .addController(controller)
                                         .getTasks();

    Scheduler scheduler = newSchedulerBuilder().setPingDelayMillis(500)
                                               .setDefaultExecutionPoolSize(100)
                                               .setExecutionPoolSize("large", 1000)
                                               .setThrowCatcher((throwable -> {
                                                 System.out.println("Error in scheduler method come here");
                                                 throwable.printStackTrace(System.out);
                                               }))
                                               .addTasks(tasks)
                                               .build();

    System.out.println("t3884tR1De :: Started UP");

    scheduler.startup();

    fui.go();

    scheduler.shutdown();

    System.out.println("ws9oaZk1ng :: Shut Down");

    Thread.sleep(4 * 1000);

    System.out.println("Mo2ZwCc4k2 :: By by");
  }
}
