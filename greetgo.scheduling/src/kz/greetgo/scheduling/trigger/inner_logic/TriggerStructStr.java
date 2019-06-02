package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.TriggerParseError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TriggerStructStr implements TriggerStruct {

  private final String source;
  public final Range range;

  public TriggerStructStr(String source, Range range) {
    this.source = source;
    this.range = range;
  }

  public String source() {
    return source.trim();
  }

  @Override
  public Range range() {
    return range;
  }

  @Override
  public String toString() {
    return "[" + source() + "]";
  }

  private Trigger trigger;

  private List<ParseError> errorList = new ArrayList<>();

  @Override
  public List<TriggerParseError> errors(Range top, String triggerString) {
    trigger();
    return errorList.stream().map(e -> convertError(top, e, triggerString)).collect(Collectors.toList());
  }

  private TriggerParseError convertError(Range top, ParseError parseError, String triggerString) {
    return new TriggerParseError() {
      @Override
      public String errorMessage() {
        return parseError.message;
      }

      @Override
      public String errorCode() {
        return parseError.errorCode;
      }

      @Override
      public Range errorPlace() {
        return top.up(range.up(parseError.range));
      }

      @Override
      public String triggerString() {
        return triggerString;
      }
    };
  }

  @Override
  public Trigger trigger() {
    {
      Trigger trigger = this.trigger;
      if (trigger != null) {
        return trigger;
      }
    }
    {
      TriggerStructStrParser parser = TriggerStructStrParser.of(range, source);
      errorList = parser.errorList;
      return trigger = parser.parse();
    }
  }

}
