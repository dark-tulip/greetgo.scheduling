package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.TriggerParseError;

import java.util.List;

import static kz.greetgo.scheduling.util.ListUtil.concatLists;

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

  @Override
  public Trigger trigger() {
    return new Trigger() {
      Trigger triggerA = a.trigger();
      Trigger triggerB = b.trigger();

      @Override
      public boolean isHit(long startedAt, long from, long to) {
        return triggerA.isHit(startedAt, from, to) && triggerB.isHit(startedAt, from, to);
      }

      @Override
      public boolean isDotty() {
        return triggerA.isDotty() || triggerB.isDotty();
      }

      @Override
      public String toString() {
        return "(" + triggerA + " * " + triggerB + ")";
      }
    };
  }

  @Override
  public List<TriggerParseError> errors(Range top, String triggerString) {
    return concatLists(a.errors(top, triggerString), b.errors(top, triggerString));
  }

}
