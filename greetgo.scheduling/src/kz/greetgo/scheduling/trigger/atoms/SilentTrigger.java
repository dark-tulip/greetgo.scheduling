package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

public class SilentTrigger implements Trigger {

  public static final SilentTrigger SILENT = new SilentTrigger();

  public SilentTrigger() {}

  @Override
  public boolean isHit(long startedSchedulerMillisAt, long timeMillisFrom, long timeMillisTo) {
    return false;
  }

  @Override
  public boolean isDotty() {
    return false;
  }

  @Override
  public String toString() {
    return "Silent";
  }

}
