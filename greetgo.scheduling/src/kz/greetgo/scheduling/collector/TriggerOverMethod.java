package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import kz.greetgo.scheduling.trigger.TriggerParser;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;

public class TriggerOverMethod implements Trigger {

  private final ScheduledDefinition definition;
  private final ControllerContext context;

  private final Trigger fixedTrigger;

  private TriggerOverMethod(ScheduledDefinition definition, ControllerContext context) {

    this.context = context;
    this.definition = definition;

    if (definition.isFromFile) {
      fixedTrigger = null;
    } else {
      TriggerParseResult parseResult = TriggerParser.parse(definition.patternFromAnnotation);
      ScheduledParseException.check(parseResult.errors());
      fixedTrigger = parseResult.trigger();
    }

  }

  private Trigger trigger() {
    return fixedTrigger != null ? fixedTrigger : context.trigger(definition.name);
  }

  public static TriggerOverMethod create(ScheduledDefinition definition, ControllerContext context) {
    return new TriggerOverMethod(definition, context);
  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {
    return trigger().isHit(schedulerStartedAtMillis, timeMillisFrom, timeMillisTo);
  }

  @Override
  public boolean isDotty() {
    return trigger().isDotty();
  }

  @Override
  public boolean isParallel() {
    return trigger().isParallel();
  }

  @Override
  public String toString() {
    return "Wrapper[" + trigger() + "]";
  }

}
