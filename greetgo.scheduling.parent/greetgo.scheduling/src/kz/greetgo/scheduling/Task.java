package kz.greetgo.scheduling;

public class Task {

  private final String poolName;
  private final Job job;
  private final Trigger trigger;
  private final ThrowableCatcher throwableCatcher;

  public Task(String poolName, Job job, Trigger trigger, ThrowableCatcher throwableCatcher) {
    if (job == null) throw new IllegalArgumentException("job == null");
    if (trigger == null) throw new IllegalArgumentException("trigger == null");
    if (poolName == null) throw new IllegalArgumentException("poolName == null");
    if (throwableCatcher == null) throw new IllegalArgumentException("throwableCatcher == null");
    this.poolName = poolName;
    this.job = job;
    this.trigger = trigger;
    this.throwableCatcher = throwableCatcher;
  }

  public void run() {
    trigger.jobIsGoingToStart();
    try {
      job.doWork();
    } catch (Throwable e) {
      throwableCatcher.catchThrowable(e);
    }
    trigger.jobHasFinishedJustNow();
  }

  public boolean mayParallel() {
    return trigger.mayParallel();
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

  public void schedulerStarted() {
    trigger.schedulerIsStartedJustNow();
  }

  public boolean disabled() {
    return trigger.disabled();
  }

  @Override
  public String toString() {
    return infoForError() + " " + trigger;
  }

  public void markThatInExecutionQueue() {
    trigger.markThatInExecutionQueue();
  }
}
