package kz.greetgo.scheduling.trigger;

import java.util.Objects;

public class Range {
  public final int from;
  public final int to;

  public Range(int from, int to) {
    this.from = from;
    this.to = to;
  }

  public static Range of(int from, int to) {
    return new Range(from, to);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Range range = (Range) o;
    return from == range.from &&
      to == range.to;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

  public String cut(String str) {
    return str.substring(from, to);
  }

  public Range union(Range a) {
    return new Range(Math.min(from, a.from), Math.max(to, a.to));
  }

  @Override
  public String toString() {
    return "Range{" + from + "..." + to + "}";
  }
}
