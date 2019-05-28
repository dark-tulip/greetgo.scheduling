package kz.greetgo.scheduling.trigger;

public class Range {
  public final int from;
  public final int to;

  public Range(int from, int to) {
    this.from = from;
    this.to = to;
  }

  public String cut(String str) {
    return str.substring(from, to);
  }

  public Range union(Range a) {
    return new Range(Math.min(from, a.from), Math.max(to, a.to));
  }
}
