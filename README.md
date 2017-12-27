# greetgo.scheduling

Запуск задач по расписанию

### Быстрый старт

Подключаем библиотеку:

    compile "kz.greetgo:greetgo.scheduling:2.0.0"

Нужны такие импорты:

```java
import kz.greetgo.scheduling.ExecutionPool;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.Scheduler;
import kz.greetgo.scheduling.Task;
import kz.greetgo.scheduling.TaskCollector;
import kz.greetgo.scheduling.ThrowableCatcher;
import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.UsePool;
```

Создатём класс с публичными методами, помеченными аннотацией @Scheduled:

```java
public class SomeScheduledClass {
  
  @Scheduled("запускать каждые 10 секунд")//фиксированное расписание
  public void task1() {
    System.out.println("Идёт работа таски №1");
  }
  
  @FromConfig("Таска номер 2")//расписание будет браться из файла-конфига dir/to/configs/SomeScheduledClass.hotconfig
  //если файла-конфига нет, то он создастся автоматически
  @Scheduled("запускать каждые 20 секунд")//а это расписание по-умолчанию
  public void task2() {//имя метода - это ключ в файле конфиге : значение будет присваиваться по-умолчанию
    System.out.println("Идёт работа таски №2");
  }
  
  @FromConfig("Таска номер 3")
  @UsePool("Hello")//эта задача будет запускаться в пуле Hello. Другие задачи: в пуле по-умолчанию
  @Scheduled("запускать каждые 5 секунд")
  public void task3() {
    System.out.println("Идёт работа таски №3");
  }
  
}

```

(таких классов может быть много)

И потом запускаем шедулер таким способом:

```java
public class RunScheduler {
  public static void main(String[] args) {
    
    //Вначале нужно собрать таски с помощью объекта
    TaskCollector taskCollector = new TaskCollector("dir/to/configs");
    
    // в папке dir/to/configs будут создаваться конфиги для тасков, помеченные аннотацией @FromConfig
    
    // указываем, как будут обрабатываться исключения в тасках
    taskCollector.throwableCatcher = new ThrowableCatcher() {
      @Override
      public void catchThrowable(Throwable throwable) {
        System.out.println("Wow " + throwable);
      }
    };
    
    //теперь собираем таски
    
    SomeScheduledClass x = new SomeScheduledClass();
    
    taskCollector.collect(x);

    //  ...так же передаём и другие объекты с аннотациями @Scheduled в taskCollector.collect
    
    // Теперь собираем пулы, которые нужны таскам
    
    Map<String, ExecutionPool> executionPoolMap = ExecutionPool.poolsForTasks(tasks);
    
    // Создаём объект-исполнитель расписаний, который и будет запускать наши таски по расписанию
    // и следить за изменениями в конфигурационных файлах, за изменением расписаний
    
    Scheduler scheduler = new Scheduler(tasks, executionPoolMap);
    
    
    // Ну а теперь запускаем сам процесс запуска тасков по расписанию
    scheduler.startup();
    
    //с этого момента начинат запускаться таски по расписанию...
    
    //Если запускание тасков больше не нужно - останавливаем процесс так:
    scheduler.shutdown();
    
  }
}
```