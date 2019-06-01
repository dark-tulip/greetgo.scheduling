package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.Trigger;

import static kz.greetgo.scheduling.util.TimeUtil.millisFromDayBegin;

public class TriggerPeriodInDayRepeat implements Trigger {

  private final TriggerPeriodInDay period;
  private final long delayMillis;

  public TriggerPeriodInDayRepeat(TriggerPeriodInDay period, long delayMillis) {
    this.period = period;
    this.delayMillis = delayMillis;
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {

    boolean periodHit = period.isHit(schedulerStartedAtMillis, timeMillisFrom, timeMillisTo);

    if (!periodHit) {
      return false;
    }

    long delta = timeMillisTo - timeMillisFrom;

    long millisFrom = millisFromDayBegin(timeMillisFrom);
    long millisTo = millisFrom + delta;

    long fromTimes = millisFrom / delayMillis;
    long toTimes = millisTo / delayMillis;

    return fromTimes != toTimes;

  }

  @Override
  public boolean isDotty() {
    return true;
  }

}
