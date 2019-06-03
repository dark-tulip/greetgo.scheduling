package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.atoms.SilentTrigger;
import kz.greetgo.scheduling.trigger.inner_logic.Range;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParserStructuring;

import java.util.Collections;
import java.util.List;

public class TriggerParser {

  private interface ParallelParseResult {

    Range range();

    boolean isParallel();

  }

  public static TriggerParseResult parse(String triggerString) {

    if (triggerString == null) {
      return silentResult;
    }

    if (isCommented(triggerString)) {
      return silentResult;
    }

    ParallelParseResult ppr = parseParallel(triggerString);

    TriggerParserStructuring struct = TriggerParserStructuring.of(ppr.range(), triggerString);

    struct.makeResult();

    if (struct.result != null && struct.errors.isEmpty() && !struct.result.isDotty()) {
      return notDottyError(triggerString);
    }

    return new TriggerParseResult() {
      final Trigger trigger = wrap(ppr, struct.result);

      @Override
      public Trigger trigger() {
        return trigger;
      }

      @Override
      public List<TriggerParseError> errors() {
        return struct.errors;
      }

    };
  }

  private static Trigger wrap(ParallelParseResult ppr, Trigger input) {
    return new Trigger() {
      @Override
      public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {
        return input.isHit(schedulerStartedAtMillis, timeMillisFrom, timeMillisTo);
      }

      @Override
      public boolean isDotty() {
        return input.isDotty();
      }

      @Override
      public boolean isParallel() {
        return ppr.isParallel();
      }

      @Override
      public String toString() {
        return input.toString();
      }
    };
  }

  private static ParallelParseResult parseParallel(String triggerString) {

    int i1 = -1, i2 = -1;

    for (int i = 0, len = triggerString.length(); i < len; i++) {

      boolean isWhitespace = Character.isWhitespace(triggerString.charAt(i));

      if (isWhitespace) {

        if (i1 >= 0) {
          break;
        }

      } else {

        i2 = i;
        if (i1 < 0) {
          i1 = 0;
        }

      }

    }

    if (i1 < 0) {
      return getSequenceParseResult(triggerString);
    }

    String firstWord = triggerString.substring(i1, i2 + 1).toLowerCase();

    if (firstWord.startsWith("парал") || firstWord.equals("parallel")) {
      return getParallelParseResult(triggerString, i2);
    }

    return getSequenceParseResult(triggerString);
  }

  private static ParallelParseResult getSequenceParseResult(String triggerString) {
    return new ParallelParseResult() {
      final Range range = Range.of(0, triggerString.length());

      @Override
      public Range range() {
        return range;
      }

      @Override
      public boolean isParallel() {
        return false;
      }
    };
  }

  private static ParallelParseResult getParallelParseResult(String triggerString, int i2) {
    return new ParallelParseResult() {
      final int fromIndex = i2 + 1;
      final Range range = Range.of(fromIndex, triggerString.length());

      @Override
      public Range range() {
        return range;
      }

      @Override
      public boolean isParallel() {
        return true;
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
