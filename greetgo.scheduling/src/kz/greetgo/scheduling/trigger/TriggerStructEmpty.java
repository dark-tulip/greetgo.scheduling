package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.atoms.SilentTrigger;

public class TriggerStructEmpty implements TriggerStruct {

  @Override
  public Range range() {
    return new Range(0, 0);
  }

  private final Trigger trigger = new SilentTrigger(false);

  @Override
  public Trigger trigger() {
    return trigger;
  }

  @Override
  public String toString() {
    return "EMPTY";
  }

}
