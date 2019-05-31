package kz.greetgo.scheduling.trigger;

public interface Trigger {

  boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo);

  boolean isDotty();

}
