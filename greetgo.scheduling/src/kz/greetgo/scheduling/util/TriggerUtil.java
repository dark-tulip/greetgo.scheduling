package kz.greetgo.scheduling.util;

import kz.greetgo.scheduling.trigger.atoms.SilentTrigger;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.Collection;
import java.util.Objects;

public class TriggerUtil {

  public static Trigger or(Trigger a, Trigger b) {
    if (isSilent(a) && isSilent(b)) {
      return SilentTrigger.SILENT;
    }
    if (isSilent(a)) {
      return b;
    }
    if (isSilent(b)) {
      return a;
    }
    return new Trigger() {
      @Override
      public boolean isHit(long l1, long l2, long l3) {
        return a.isHit(l1, l2, l3) || b.isHit(l1, l2, l3);
      }

      @Override
      public boolean isDotty() {
        return a.isDotty() && b.isDotty();
      }

      @Override
      public String toString() {
        return "(" + a + " or " + b + ")";
      }

    };
  }

  public static Trigger ands(Collection<Trigger> triggers) {
    return triggers.stream()
                   .filter(Objects::nonNull)
                   .reduce(TriggerUtil::and)
                   .orElseGet(SilentTrigger::new);
  }

  public static Trigger ors(Collection<Trigger> triggers) {
    return triggers.stream()
                   .filter(Objects::nonNull)
                   .reduce(TriggerUtil::or)
                   .orElseGet(SilentTrigger::new);
  }

  public static boolean isSilent(Trigger x) {
    return x == null || x instanceof SilentTrigger;
  }

  public static Trigger and(Trigger a, Trigger b) {
    if (isSilent(a)) {
      return SilentTrigger.SILENT;
    }
    if (isSilent(b)) {
      return SilentTrigger.SILENT;
    }
    return new Trigger() {
      @Override
      public boolean isHit(long l1, long l2, long l3) {
        return a.isHit(l1, l2, l3) && b.isHit(l1, l2, l3);
      }

      @Override
      public boolean isDotty() {
        return a.isDotty() || b.isDotty();
      }

      @Override
      public String toString() {
        return "(" + a + " and " + b + ")";
      }

    };
  }

}
