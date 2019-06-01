package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.Trigger;
import kz.greetgo.scheduling.util.StrUtil;
import kz.greetgo.scheduling.util.TimeUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TriggerDayPoint implements Trigger {

  private final long startFromDay;
  private final String point;

  public TriggerDayPoint(int hour, int minute, int second) {
    startFromDay = toStart(hour, minute, second);
    point = StrUtil.toLenZero(hour, 2) + ":" + StrUtil.toLenZero(minute, 2) + ":" + StrUtil.toLenZero(second, 2);
  }

  public TriggerDayPoint(String hms) {
    startFromDay = TimeUtil.hmsToMillis(hms);
    point = TimeUtil.millisToHms(startFromDay);
  }

  @Override
  public String toString() {
    return "DayPoint{" + point + "}";
  }

  @Override
  public boolean isDotty() {
    return true;
  }


  private static final long MILLISECONDS_PER_SECOND = 1000;
  private static final long MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND * 60;
  private static final long MILLISECONDS_PER_HOUR = MILLISECONDS_PER_MINUTE * 60;
  private static final long MILLISECONDS_PER_DAY = MILLISECONDS_PER_HOUR * 24;

  private static long toStart(int hour, int minute, int second) {
    return hour * MILLISECONDS_PER_HOUR + minute * MILLISECONDS_PER_MINUTE + second * MILLISECONDS_PER_SECOND;
  }

  private static boolean isIn(long point, long from, long delta) {
    return from <= point && point <= from + delta;
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {

    long delta = timeMillisTo - timeMillisFrom;

    Calendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(timeMillisFrom);
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);

    long from = toStart(hour, minute, second);

    return isIn(startFromDay, from, delta) || isIn(startFromDay + MILLISECONDS_PER_DAY, from, delta);
  }

}
