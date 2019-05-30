package kz.greetgo.scheduling.trigger;

public class SilentTrigger implements Trigger {

  private final boolean dotty;

  public SilentTrigger(boolean dotty) {
    this.dotty = dotty;
  }

  @Override
  public boolean isHit(long startedSchedulerMillisAt, long timeMillisFrom, long timeMillisTo) {
    return false;
  }

  @Override
  public boolean isDotty() {
    return dotty;
  }

  @Override
  public String toString() {
    return "Silent{dotty=" + dotty + '}';
  }

}
