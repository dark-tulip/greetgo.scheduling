package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.TriggerParseError;

import java.util.List;

public interface TriggerStruct extends ExpressionElement {

  Trigger trigger();

  List<TriggerParseError> errors(String triggerString);

}
