package kz.greetgo.scheduling;

public abstract class AbstractTrigger implements Trigger {
  @Override
  public void schedulerIsStartedJustNow() {
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
}
