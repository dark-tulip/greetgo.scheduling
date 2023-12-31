package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.trigger.inner_logic.WeekDay;
import kz.greetgo.scheduling.util.CalendarEq;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TriggerWeekDay implements Trigger {

  private final WeekDay weekDay;

  public TriggerWeekDay(WeekDay weekDay) {
    this.weekDay = weekDay;
  }

  @Override
  public String toString() {
    return "WeekDay{" + weekDay + "}";
  }

  @Override
  public boolean isDotty() {
    return false;
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {

    Calendar calendarFrom = new GregorianCalendar();
    Calendar calendarTo   = new GregorianCalendar();

    calendarFrom.setTimeInMillis(timeMillisFrom);
    calendarTo.setTimeInMillis(timeMillisTo);

    for (int i = 0; i < 7; i++) {
      if (calendarFrom.get(Calendar.DAY_OF_WEEK) == weekDay.calendar) {
        return true;
      }

      if (CalendarEq.of(calendarFrom).ymdEquals(calendarTo)) {
        return false;
      }

      calendarFrom.add(Calendar.DAY_OF_YEAR, 1);
    }

    return false;
  }

}
