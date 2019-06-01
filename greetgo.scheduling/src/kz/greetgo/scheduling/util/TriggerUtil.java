package kz.greetgo.scheduling.util;

import kz.greetgo.scheduling.trigger.Trigger;

import java.util.List;

public class TriggerUtil {
  public static Trigger orList(List<Trigger> triggers) {
    if (triggers.isEmpty()) {
      throw new IllegalArgumentException("triggers is empty");
    }

    if (triggers.size() == 1) {
      return triggers.get(0);
    }

    Trigger ret = triggers.get(0);

    for (int i = 1, c = triggers.size(); i < c; i++) {
      ret = or(ret, triggers.get(i));
    }

    return ret;
  }

  public static Trigger or(Trigger a, Trigger b) {
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
        return "(" + a.toString() + " or " + b.toString() + ")";
      }

    };
  }

  public static Trigger andList(List<Trigger> triggers) {
    if (triggers.isEmpty()) {
      throw new IllegalArgumentException("triggers is empty");
    }

    if (triggers.size() == 1) {
      return triggers.get(0);
    }

    Trigger ret = triggers.get(0);

    for (int i = 1, c = triggers.size(); i < c; i++) {
      ret = and(ret, triggers.get(i));
    }

    return ret;
  }

  public static Trigger and(Trigger a, Trigger b) {
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
        return "(" + a.toString() + " and " + b.toString() + ")";
      }

    };
  }

}
