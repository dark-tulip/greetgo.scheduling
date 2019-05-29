package kz.greetgo.scheduling.trigger;

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

  private Trigger trigger = null;

  @Override
  public Trigger trigger() {
    {
      Trigger trigger = this.trigger;
      if (trigger != null) {
        return trigger;
      }
    }
    {
      return trigger = TriggerStructStrParser.of(range, source, System::currentTimeMillis).parse();
    }
  }

}
