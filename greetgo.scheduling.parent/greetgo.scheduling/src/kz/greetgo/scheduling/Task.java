package kz.greetgo.scheduling;

public class Task {

  private final String poolName;
  private final boolean mayParallel;
  private final Job job;
  private final Trigger trigger;
  private final ThrowableCatcher throwableCatcher;

  public Task(String poolName, boolean mayParallel, Job job, Trigger trigger, ThrowableCatcher throwableCatcher) {
    this.poolName = poolName;
    this.mayParallel = mayParallel;
    this.job = job;
    this.trigger = trigger;
    this.throwableCatcher = throwableCatcher;
  }

  public void run() {
    try {
      job.doWork();
    } catch (Throwable e) {
      throwableCatcher.catchThrowable(e);
    }
  }

  public boolean mayParallel() {
    return mayParallel;
  }

  public String getPoolName() {
    return poolName;
  }

  /**
   * Должен возвращать информацию о таске, чтобы понятно было где она создана. Используется в сообщениях об ошибках
   *
   * @return информация о таске
   */
  public String infoForError() {
    return job.infoForError();
  }

  public boolean isItTimeToRun() {
    return trigger.isItTimeToRun();
  }

  public void start() {
    trigger.start();
  }
}
