package kz.greetgo.scheduling;

public class Task {

  private final String poolName;
  private final Job job;
  private final Trigger trigger;
  private final ThrowableCatcher throwableCatcher;
  final TaskRunStatus taskRunStatus;

  public Task(String poolName, Job job, Trigger trigger, ThrowableCatcher throwableCatcher) {
    if (job == null) {
      throw new IllegalArgumentException("job == null");
    }
    if (trigger == null) {
      throw new IllegalArgumentException("trigger == null");
    }
    if (poolName == null) {
      throw new IllegalArgumentException("poolName == null");
    }
    if (throwableCatcher == null) {
      throw new IllegalArgumentException("throwableCatcher == null");
    }
    this.poolName = poolName;
    this.job = job;
    this.trigger = trigger;
    this.throwableCatcher = throwableCatcher;
    taskRunStatus = trigger.getTaskRunStatus();
  }

  public void run() {
    trigger.jobIsGoingToStart();
    try {
      job.doWork();
    } catch (Throwable e) {
      throwableCatcher.catchThrowable(e);
    } finally {
      taskRunStatus.markFinished();
      trigger.jobHasFinishedJustNow();
    }
  }

  public String getPoolName() {
    return poolName;
  }

  /**
   * Returns task information about where is it created. It is used in error messages.
   *
   * @return task information
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

  public void resetTrigger() {
    trigger.reset();
  }

  public boolean isTriggerResettable() {
    return trigger.isResettable();
  }

  public void ping() {
    trigger.isItTimeToRun();
  }
}
