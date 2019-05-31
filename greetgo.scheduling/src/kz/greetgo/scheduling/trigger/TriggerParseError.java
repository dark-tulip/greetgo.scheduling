package kz.greetgo.scheduling.trigger;

public interface TriggerParseError {
  String getMessage();

  int positionStart();

  int positionEnd();

  String triggerString();
}
