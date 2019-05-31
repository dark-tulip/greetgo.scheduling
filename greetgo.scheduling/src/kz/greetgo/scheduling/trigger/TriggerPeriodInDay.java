package kz.greetgo.scheduling.trigger;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TriggerPeriodInDay implements Trigger {

  public static final long MILLIS_SECOND = 1000;
  public static final long MILLIS_MINUTE = MILLIS_SECOND * 60;
  public static final long MILLIS_HOUR = MILLIS_MINUTE * 60;
  private final long millisInDayFrom;
  private final long millisInDayTo;

  public static long hmsToMillis(String hmsStr) {
    String[] split = hmsStr.split(":");

    if (split.length != 2 && split.length != 3) {
      throw new RuntimeException("Illegal TIME_OF_DAY value");
    }

    long hours = Long.parseLong(split[0]);
    long minutes = Long.parseLong(split[1]);
    long seconds = 0;
    if (split.length >= 3) {
      seconds = Long.parseLong(split[2]);
    }

    return longYmsToMillis(hours, minutes, seconds);
  }

  public static long longYmsToMillis(long hours, long minutes, long seconds) {
    return MILLIS_SECOND * seconds + MILLIS_MINUTE * minutes + MILLIS_HOUR * hours;
  }

  public TriggerPeriodInDay(long millisInDayFrom, long millisInDayTo) {
    this.millisInDayFrom = millisInDayFrom;
    this.millisInDayTo = millisInDayTo;
  }

  public TriggerPeriodInDay(String hmsFrom, String hmsTo) {
    this(hmsToMillis(hmsFrom), hmsToMillis(hmsTo));
  }

  @Override
  public boolean isDotty() {
    return false;
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {

    Calendar c = new GregorianCalendar();
    c.setTimeInMillis(timeMillisFrom);

    int hours = c.get(Calendar.HOUR_OF_DAY);
    int minutes = c.get(Calendar.MINUTE);
    int seconds = c.get(Calendar.SECOND);
    long millisFrom = longYmsToMillis(hours, minutes, seconds);

    long delta = timeMillisTo - timeMillisFrom;

    long millisTo = millisFrom + delta;

    return isIntersect(millisFrom, millisTo, millisInDayFrom, millisInDayTo);
  }

  public static boolean isIntersect(long from1, long to1, long from2, long to2) {

    if (from1 > to1) {
      long tmp = from1;
      from1 = to1;
      to1 = tmp;
    }
    if (from2 > to2) {
      long tmp = from2;
      from2 = to2;
      to2 = tmp;
    }

    return to1 >= from2 && to2 >= from1;

  }
}
