package kz.greetgo.scheduling.trigger;

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

    Calendar calendar = new GregorianCalendar();

    calendar.setTimeInMillis(timeMillisTo);
    int endYear = calendar.get(Calendar.YEAR);
    int endMonth = calendar.get(Calendar.MONTH);
    int endDay = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.setTimeInMillis(timeMillisFrom);

    for (int i = 0; i < 7; i++) {
      if (calendar.get(Calendar.DAY_OF_WEEK) == weekDay.calendar) {
        return true;
      }

      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int day = calendar.get(Calendar.DAY_OF_MONTH);

      if (endYear == year && endMonth == month && endDay == day) {
        return false;
      }

      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }

    return false;
  }

}
