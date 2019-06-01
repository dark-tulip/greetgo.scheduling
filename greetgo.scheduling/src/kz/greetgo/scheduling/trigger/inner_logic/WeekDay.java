package kz.greetgo.scheduling.trigger.inner_logic;

import java.util.Calendar;

public enum WeekDay {

  MONDAY(Calendar.MONDAY),
  TUESDAY(Calendar.TUESDAY),
  WEDNESDAY(Calendar.WEDNESDAY),
  THURSDAY(Calendar.THURSDAY),
  FRIDAY(Calendar.FRIDAY),
  SATURDAY(Calendar.SATURDAY),
  SUNDAY(Calendar.SUNDAY),

  ;

  public final int calendar;

  WeekDay(int calendar) {
    this.calendar = calendar;
  }

}
