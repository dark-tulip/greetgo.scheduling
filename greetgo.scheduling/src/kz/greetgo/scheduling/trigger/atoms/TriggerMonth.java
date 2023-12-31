package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Range;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.util.CalendarEq;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class TriggerMonth implements Trigger {

  private final Range range;

  public TriggerMonth(Range range) {
    this.range = Objects.requireNonNull(range);
  }

  @Override
  public String toString() {
    return "Month{" + range.from + ".." + range.to + "}";
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

    int calendarMonthFrom = range.from - 1;
    int calendarMonthTo   = range.to - 1;

    while (true) {
      int calendarMonth = calendarFrom.get(Calendar.MONTH);
      if (calendarMonthFrom <= calendarMonth && calendarMonth <= calendarMonthTo) {
        return true;
      }

      if (CalendarEq.of(calendarFrom).ymdEquals(calendarTo)) {
        return false;
      }

      calendarFrom.add(Calendar.DAY_OF_YEAR, 1);
    }
  }

}
