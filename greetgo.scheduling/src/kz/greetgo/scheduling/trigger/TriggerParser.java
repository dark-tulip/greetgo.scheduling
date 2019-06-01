package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.atoms.SilentTrigger;
import kz.greetgo.scheduling.trigger.inner_logic.Range;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring;

import java.util.Collections;
import java.util.List;

public class TriggerParser {

  public static TriggerParseResult parse(String triggerString) {

    if (isCommented(triggerString)) {
      return silentResult;
    }

    TriggerParserStructuring struct = new TriggerParserStructuring(triggerString);
    struct.makeResult();

    if (struct.result != null && struct.errors.isEmpty() && !struct.result.isDotty()) {
      return notDottyError(triggerString);
    }

    return new TriggerParseResult() {
      @Override
      public Trigger trigger() {
        return struct.result;
      }

      @Override
      public List<TriggerParseError> errors() {
        return struct.errors;
      }
    };
  }

  private static TriggerParseResult notDottyError(String triggerString) {
    return new TriggerParseResult() {
      final Trigger silent = new SilentTrigger();

      @Override
      public Trigger trigger() {
        return silent;
      }

      @Override
      public List<TriggerParseError> errors() {
        return Collections.singletonList(new TriggerParseError() {
          @Override
          public String errorMessage() {
            return "Расписание должно указывать моменты времени, а не промежутки";
          }

          @Override
          public String errorCode() {
            return "h180sws";
          }

          @Override
          public Range errorPlace() {
            return Range.of(0, triggerString.length());
          }

          @Override
          public String triggerString() {
            return triggerString;
          }
        });
      }
    };
  }

  private static final TriggerParseResult silentResult = new TriggerParseResult() {
    final Trigger silent = new SilentTrigger();

    @Override
    public Trigger trigger() {
      return silent;
    }

    @Override
    public List<TriggerParseError> errors() {
      return Collections.emptyList();
    }
  };

  private static boolean isCommented(String triggerString) {
    return triggerString == null || triggerString.trim().startsWith("#")
      || triggerString.trim().toLowerCase().startsWith("off");
  }

}
