package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import kz.greetgo.scheduling.trigger.TriggerParser;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;

public class TriggerOverMethod implements Trigger {

  private final ScheduledDefinition definition;
  private final ControllerContext context;

  private final Trigger fixedTrigger;

  private TriggerOverMethod(String methodName,
                            Scheduled scheduled, FromConfig fromConfig,
                            ControllerContext context) {

    this.context = context;

    if (fromConfig == null) {
      definition = new ScheduledDefinition(methodName, scheduled.value(), false, null);
    } else {
      definition = new ScheduledDefinition(methodName, scheduled.value(), true, fromConfig.value());
    }

    if (definition.isFromFile) {
      fixedTrigger = null;
    } else {
      TriggerParseResult parseResult = TriggerParser.parse(definition.patternFromAnnotation);
      ScheduledParseException.check(parseResult.errors());
      fixedTrigger = parseResult.trigger();
    }

  }

  private Trigger trigger() {
    return fixedTrigger != null ? fixedTrigger : context.trigger(definition);
  }

  public static TriggerOverMethod create(String methodName,
                                         Scheduled scheduled, FromConfig fromConfig,
                                         ControllerContext context) {

    return new TriggerOverMethod(methodName, scheduled, fromConfig, context);

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
