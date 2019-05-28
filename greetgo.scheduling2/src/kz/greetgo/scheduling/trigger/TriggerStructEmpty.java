package kz.greetgo.scheduling.trigger;

public class TriggerStructEmpty implements TriggerStruct {
  @Override
  public Range range() {
    return new Range(0, 0);
  }

  @Override
  public String toString() {
    return "TRIGGER_EMPTY";
  }
}
