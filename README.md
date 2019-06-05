# greetgo.scheduling

Run tasks by scheduling

### Quick start

Include library:

    compile "kz.greetgo:greetgo.scheduling:3.0.0"

Using next imports:

```java
import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.UsePool;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.collector.SchedulerConfigStore;
import kz.greetgo.scheduling.collector.SchedulerConfigStoreInFile;
import kz.greetgo.scheduling.collector.Task;

import static kz.greetgo.scheduling.collector.TaskCollector.newTaskCollector;
import static kz.greetgo.scheduling.scheduler.SchedulerBuilder.newSchedulerBuilder;
```

Creates class with public method, marked with annotation @Scheduled:

```java
public class SomeScheduledClass {
  
  @Scheduled("repeat every 10 seconds")
  public void task1() {
    System.out.println("Task №1 is working...");
  }
  
  @FromConfig("Task number 2 description")//scheduler takes from config dir/to/configs/SomeScheduledClass.sch.txt
  //If config is absent, it will be created automatically
  @Scheduled("repeat every 20 seconds")//it is scheduling by default
  public void task2() {//method name - key in file config : value will be from @Scheduled
    System.out.println("Task №2 is working...");
  }
  
  @FromConfig("Description of task number 3")
  @UsePool("Hello")//This task will be pul in execution pool `Hello`
  @Scheduled("repeat every 5 seconds")
  public void task3() {
    System.out.println("Task №3 is working...");
  }
  
}

```

(such classes may be many)

And then run scheduler as following:

```java
public class RunScheduler {
  public static void main(String[] args) {
    
    //dir for config files
    Path configRoot = Paths.get("dir/to/configs");
    
    //Creates config storage in files. You can implement SchedulerConfigStore to store configs where ever else
    SchedulerConfigStore configStore = new SchedulerConfigStoreInFile(configRoot);
    
    //Takes controllers
    SomeScheduledClass x = new SomeScheduledClass();
    SomeScheduledClass2 y = new SomeScheduledClass2();
    
    // Now creates tasks
    List<Task> tasks = newTaskCollector()
          .setSchedulerConfigStore(configStore)
          .setConfigExtension(".cfg.txt")//config extension
          .setConfigErrorsExtension(".cfg.error.txt")//extension for errors from config
          .addController(x)//register controllers
          .addController(y)
          .getTasks();//generate and get tasks
    
    // Creates scheduler
    Scheduler scheduler = newSchedulerBuilder()
          .setPingDelayMillis(200)//check task run delay
          .setDefaultExecutionPoolSize(100)//max 
          .setExecutionPoolSize("large", 1000)
          .setThrowCatcher((throwable -> {
            System.out.println("Error in scheduler method come here");
            throwable.printStackTrace(System.out);
          }))
          .addTasks(tasks)
          .build();
    
    // Start scheduler for all tasks
    scheduler.startup();
    //from here all task will be started by their scheduler
    
    //...
    //...
    //...
    
    //Stop all task running, if need
    scheduler.shutdown();
    
  }
}
```
