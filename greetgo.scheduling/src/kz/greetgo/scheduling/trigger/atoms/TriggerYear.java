package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Range;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TriggerYear implements Trigger {

  private final Range range;

  public TriggerYear(Range range) {
    this.range = range;
  }

  @Override
  public String toString() {
    return "Year{" + range.from + ".." + range.to + "}";
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
      int year = calendarFrom.get(Calendar.YEAR);
      if (range.from <= year && year <= range.to) {
        return true;
      }

      if (calendarFrom.get(Calendar.YEAR) == calendarTo.get(Calendar.YEAR)) {
        return false;
      }

      calendarFrom.add(Calendar.YEAR, 1);
    }
  }

}
