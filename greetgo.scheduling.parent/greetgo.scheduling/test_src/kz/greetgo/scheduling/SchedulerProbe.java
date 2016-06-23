package kz.greetgo.scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SchedulerProbe {

  private static class MyTrigger implements Trigger {

    private final int everySeconds;

    public MyTrigger(int everySeconds) {
      this.everySeconds = everySeconds;
    }

    private long lastTime;

    @Override
    public boolean isItTimeToRun() {
      final long now = System.currentTimeMillis();
      if (now - lastTime < everySeconds * 1000) return false;
      lastTime = now;
      return true;
    }

    @Override
    public void schedulerIsStartedJustNow() {
      lastTime = System.currentTimeMillis();
    }

    @Override
    public void jobIsGoingToStart() {
    }

    @Override
    public void jobHasFinishedJustNow() {
    }

    @Override
    public boolean disabled() {
      return false;
    }

    @Override
    public boolean mayParallel() {
      return false;
    }

    @Override
    public void markThatInExecutionQueue() {
    }

    @Override
    public void reset() {
    }
  }

  private static class MyThrowableCatcher implements ThrowableCatcher {
    @Override
    public void catchThrowable(Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  private static class PrintHelloWorld implements Job {
    @Override
    public void doWork() throws Throwable {
      System.out.println(pre() + "Hello World!!!");
    }

    @Override
    public String infoForError() {
      return "JOB PrintHelloWorld";
    }
  }

  private static String pre() {
    return System.currentTimeMillis() + " " + Thread.currentThread().getName() + " ";
  }

  private static class CountTo implements Job {
    private final int limit;
    private final long delay;

    public CountTo(int limit, long delay) {
      this.limit = limit;
      this.delay = delay;
    }

    @Override
    public void doWork() throws Throwable {
      for (int i = 1; i <= limit; i++) {
        System.out.println(pre() + "Count " + i);
        Thread.sleep(delay);
      }
      System.out.println(pre() + "I counted to " + limit);
      throw new Exception("Oops");
    }

    @Override
    public String infoForError() {
      return "JOB1";
    }
  }

  public static void main(String[] args) throws Exception {
    final boolean inMainThread = "a".equals("a1");

    final MyThrowableCatcher tc = new MyThrowableCatcher();

    String defaultPoolName = "default";

    final Set<Task> tasks = new HashSet<>();

    {
      final PrintHelloWorld job = new PrintHelloWorld();
      final MyTrigger trigger = new MyTrigger(5);
      tasks.add(new Task(defaultPoolName, job, trigger, tc));
    }
    {
      final CountTo job = new CountTo(10, 300L);
      final MyTrigger trigger = new MyTrigger(7);
      tasks.add(new Task(defaultPoolName, job, trigger, tc));
    }

    final Map<String, ExecutionPool> pools = new HashMap<>();
    pools.put(defaultPoolName, new ExecutionPool());

    final Scheduler scheduler = new Scheduler(tasks, pools);

    if (inMainThread) {
      scheduler.startupInMyThread();
      return;
    }
    scheduler.startup();

    System.out.println(pre() + "SCHEDULER STARTED");

    Thread.sleep(20 * 1000);

    System.out.println(pre() + "____________HI____________");

    Thread.sleep(20 * 1000);

    System.out.println(pre() + "____________SHUTDOWN___________________");

    scheduler.shutdown();

    System.out.println(pre() + "____________SHUTDOWN_CALLED____________");

    Thread.sleep(5 * 1000);

    System.out.println(pre() + "____________COMPLETE___________________");

  }
}
