package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.util.TimeUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static kz.greetgo.scheduling.util.TimeUtil.hmsToMillis;
import static kz.greetgo.scheduling.util.TimeUtil.isIntersect;
import static kz.greetgo.scheduling.util.TimeUtil.millisToHms;

public class TriggerPeriodInDay implements Trigger {

  private final long millisInDayFrom;
  private final long millisInDayTo;

  public TriggerPeriodInDay(long millisInDayFrom, long millisInDayTo) {
    this.millisInDayFrom = millisInDayFrom;
    this.millisInDayTo = millisInDayTo;
  }

  public TriggerPeriodInDay(String hmsFrom, String hmsTo) {
    this(hmsToMillis(hmsFrom), hmsToMillis(hmsTo));
  }

  @Override
  public String toString() {
    return "PeriodInDay{" + millisToHms(millisInDayFrom) + "..." + millisToHms(millisInDayTo) + "}";
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
    long millisFrom = TimeUtil.longYmsToMillis(hours, minutes, seconds);

    long delta = timeMillisTo - timeMillisFrom;

    long millisTo = millisFrom + delta;

    return isIntersect(millisFrom, millisTo, millisInDayFrom, millisInDayTo);
  }

}
