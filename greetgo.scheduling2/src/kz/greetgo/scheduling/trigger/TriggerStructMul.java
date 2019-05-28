package kz.greetgo.scheduling.trigger;

public class TriggerStructMul implements TriggerStruct {

  public final TriggerStruct a;
  public final TriggerStruct b;
  private final Range range;

  public TriggerStructMul(TriggerStruct a, TriggerStruct b) {
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
    return "(" + a.toString() + " * " + b.toString() + ")";
  }

}
