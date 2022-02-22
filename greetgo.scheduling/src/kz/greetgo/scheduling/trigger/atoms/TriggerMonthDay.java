package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Range;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.util.CalendarEq;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class TriggerMonthDay implements Trigger {
  private final Range range;

  public TriggerMonthDay(Range range) {
    this.range = Objects.requireNonNull(range);
  }

  @Override
  public String toString() {
    return "MonthDay{" + range.from + ".." + range.to + "}";
  }

  @Override
  public boolean isDotty() {
    return false;
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {

    if (timeMillisFrom > timeMillisTo) {
      return false;
    }

    Calendar calendarFrom = new GregorianCalendar();
    Calendar calendarTo   = new GregorianCalendar();

    calendarFrom.setTimeInMillis(timeMillisFrom);
    calendarTo.setTimeInMillis(timeMillisTo);

    while (true) {
      int dayOfMonth = calendarFrom.get(Calendar.DAY_OF_MONTH);
      if (range.from <= dayOfMonth && dayOfMonth <= range.to) {
        return true;
      }

      if (CalendarEq.of(calendarFrom).ymEquals(calendarTo)) {
        return false;
      }

      calendarFrom.add(Calendar.MONTH, 1);
    }
  }

}
