package kz.greetgo.scheduling.trigger;

public class TriggerStructPlus implements TriggerStruct {

  public final TriggerStruct a;
  public final TriggerStruct b;

  private Range range;

  public TriggerStructPlus(TriggerStruct a, TriggerStruct b) {
    this.a = a;
    this.b = b;
    range = a.range().union(b.range());
  }

  @Override
  public Range range() {
    return range;
  }

  @Override
  public String toString() {
    return "(" + a.toString() + " + " + b.toString() + ")";
  }

}
