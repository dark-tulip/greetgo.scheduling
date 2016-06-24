package kz.greetgo.scheduling;

public interface Trigger {

  boolean isItTimeToRun();

  void schedulerIsStartedJustNow();

  void jobIsGoingToStart();

  void jobHasFinishedJustNow();

  boolean disabled();

  void markThatInExecutionQueue();

  void reset();

  boolean isResettable();

  TaskRunStatus getTaskRunStatus();
}
