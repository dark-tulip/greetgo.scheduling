package kz.greetgo.scheduling.util;

import java.util.Calendar;

public class CalendarEq {
  private final Calendar calendar;

  private CalendarEq(Calendar calendar) {
    this.calendar = calendar;
  }

  public static CalendarEq of(Calendar calendar) {
    return new CalendarEq(calendar);
  }

  public boolean ymdEquals(Calendar c2) {
    int year1  = calendar.get(Calendar.YEAR);
    int month1 = calendar.get(Calendar.MONTH);
    int day1   = calendar.get(Calendar.DAY_OF_MONTH);

    int year2  = c2.get(Calendar.YEAR);
    int month2 = c2.get(Calendar.MONTH);
    int day2   = c2.get(Calendar.DAY_OF_MONTH);

    return year1 == year2 && month1 == month2 && day1 == day2;
  }
}
