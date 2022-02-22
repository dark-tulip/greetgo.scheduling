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

  public boolean ymdEquals(Calendar other) {
    int day1 = calendar.get(Calendar.DAY_OF_MONTH);

    int day2 = other.get(Calendar.DAY_OF_MONTH);

    return day1 == day2 && ymEquals(other);
  }

  public boolean ymEquals(Calendar other) {
    int year1  = calendar.get(Calendar.YEAR);
    int month1 = calendar.get(Calendar.MONTH);

    int year2  = other.get(Calendar.YEAR);
    int month2 = other.get(Calendar.MONTH);

    return year1 == year2 && month1 == month2;
  }
}
