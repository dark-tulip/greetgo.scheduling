package kz.greetgo.scheduling;

public interface Trigger {

  boolean isItTimeToRun();

  void schedulerIsStartedJustNow();

  void jobIsGoingToStart();

  void jobHasFinishedJustNow();

  boolean disabled();

  boolean mayParallel();

  void markThatInExecutionQueue();

  void reset();
}
