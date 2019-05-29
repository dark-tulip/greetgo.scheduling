package kz.greetgo.scheduling.trigger;

public class TriggerStructStrParser {

  private final Range range;
  private final String source;
  private final CurrentTimeMillis nowSource;

  private TriggerStructStrParser(Range range, String source, CurrentTimeMillis nowSource) {
    this.range = range;
    this.source = source;
    this.nowSource = nowSource;
  }

  public static TriggerStructStrParser of(Range range, String source, CurrentTimeMillis nowSource) {
    return new TriggerStructStrParser(range, source, nowSource);
  }

  public Trigger parse() {

    long currentTimeMillis = System.currentTimeMillis();

    return null;
  }

}
