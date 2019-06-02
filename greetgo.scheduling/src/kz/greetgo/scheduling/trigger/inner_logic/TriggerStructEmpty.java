package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.TriggerParseError;
import kz.greetgo.scheduling.trigger.atoms.SilentTrigger;

import java.util.Collections;
import java.util.List;

public class TriggerStructEmpty implements TriggerStruct {

  @Override
  public Range range() {
    return new Range(0, 0);
  }

  private final Trigger trigger = new SilentTrigger();

  @Override
  public Trigger trigger() {
    return trigger;
  }

  @Override
  public String toString() {
    return "EMPTY";
  }

  @Override
  public List<TriggerParseError> errors(Range top, String triggerString) {
    return Collections.emptyList();
  }

}
