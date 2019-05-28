package kz.greetgo.scheduling.trigger;

import java.util.List;

public interface TriggerParseResult {

  Trigger trigger();

  List<TriggerParseError> errors();

}
