package kz.greetgo.scheduling.trigger;

public class TriggerRepeat implements Trigger {

  private final long startSilentMillis;
  private final long delayMillis;

  public TriggerRepeat(long startSilentMillis, long delayMillis) {
    this.startSilentMillis = startSilentMillis;
    this.delayMillis = delayMillis;
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {

    long from = timeMillisFrom - schedulerStartedAtMillis;
    long to = timeMillisTo - schedulerStartedAtMillis;

    if (from < startSilentMillis) {
      return startSilentMillis <= to;
    }

    from -= startSilentMillis;
    to -= startSilentMillis;

    long fromTimes = from / delayMillis;
    long toTimes = to / delayMillis;

    return fromTimes != toTimes;
  }

  @Override
  public boolean isDotty() {
    return true;
  }
}
