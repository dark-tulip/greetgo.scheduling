package kz.greetgo.scheduling;

public interface Trigger {

  boolean isItTimeToRun();

  void start();
}
