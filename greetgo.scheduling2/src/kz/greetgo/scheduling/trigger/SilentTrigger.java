package kz.greetgo.scheduling.trigger;

import java.util.Date;

public class SilentTrigger implements Trigger {

  private final boolean dotty;

  public SilentTrigger(boolean dotty) {
    this.dotty = dotty;
  }

  @Override
  public boolean isHit(Date periodFrom, Date periodTo) {
    return false;
  }

  @Override
  public boolean isDotty() {
    return dotty;
  }
}
