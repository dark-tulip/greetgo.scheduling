package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.inner_logic.Range;

public interface TriggerParseError {
  String errorMessage();

  String errorCode();

  Range errorPlace();

  String triggerString();
}
