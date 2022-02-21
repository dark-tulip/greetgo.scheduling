package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.util.CalendarEq;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TriggerMonth implements Trigger {

  private final int month;

  public TriggerMonth(int month) {
    this.month = month;
  }

  @Override
  public String toString() {
    return "Month{" + month + "}";
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
      if (calendarFrom.get(Calendar.MONTH) == month) {
        return true;
      }

      if (CalendarEq.of(calendarFrom).ymdEquals(calendarTo)) {
        return false;
      }

      calendarFrom.add(Calendar.DAY_OF_YEAR, 1);
    }
  }

}
