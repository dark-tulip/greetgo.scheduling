package kz.greetgo.scheduling;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchedulerMatcherRepeat implements SchedulerMatcherDelegate {

  private final ParseResult parseResult;
  private final long schedulerStartedAt;

  private SchedulerMatcherRepeat(ParseResult parseResult, long schedulerStartedAt) {
    this.parseResult = parseResult;
    this.schedulerStartedAt = schedulerStartedAt;
  }

  static class ParseResult {
    public final boolean parallel;
    public final long repeatingBy, waitingFor;

    ParseResult(boolean parallel, long repeatingBy, long waitingFor) {
      this.parallel = parallel;
      this.repeatingBy = repeatingBy;
      this.waitingFor = waitingFor;
    }
  }

  public static SchedulerMatcherDelegate parse(String pattern, long schedulerStartedAt) {
    ParseResult parseResult = parseRus(pattern);
    if (parseResult == null) parseResult = parseEng(pattern);
    if (parseResult != null) return new SchedulerMatcherRepeat(parseResult, schedulerStartedAt);
    return null;
  }


  @Override
  public boolean match(long lastCheckTime, long now) {
    if (!parseResult.parallel && taskStartedAt != null && taskFinishedAt == null) return false;

    long from = getBegin() + parseResult.waitingFor;

    if (now < from) return false;

    final long left = from + ((now - from) / parseResult.repeatingBy) * parseResult.repeatingBy;

    return lastCheckTime < left;
  }

  private long getBegin() {
    if (parseResult.parallel) return schedulerStartedAt;
    if (taskFinishedAt != null) return taskFinishedAt;
    return schedulerStartedAt;
  }

  @Override
  public boolean isParallel() {
    return parseResult.parallel;
  }

  private Long taskStartedAt = null, taskFinishedAt = null;

  @Override
  public void taskStartedAt(long taskStartedAt) {
    this.taskStartedAt = taskStartedAt;
    taskFinishedAt = null;
  }

  @Override
  public void taskFinishedAt(long taskFinishedAt) {
    this.taskFinishedAt = taskFinishedAt;
  }

  private static final Pattern RUS = Pattern.compile(
    // повторять каждые 13 мин, начиная с паузы 17 мин
    ""
      + "\\s*((paral\\w*|парал\\w*)\\s+)?"
      + "повт\\w*\\s+кажд\\w*\\s+([\\d\\.]+)\\s+(\\w+)\\s*"
      + "(,\\s*начин\\w*\\s+с\\s+пауз\\w*\\s+([\\d\\.]+)\\s+(\\w+))?\\s*"
    ,
    Pattern.CASE_INSENSITIVE | Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
  );

  static ParseResult parseRus(String pattern) {
    return parseRegexp(RUS, pattern, 2, 3, 4, 6, 7);
  }

  private static final Pattern ENG = Pattern.compile(
    // repeat every  13 minutes, after pause in 17 s
    ""
      + "\\s*((paral\\w*|парал\\w*)\\s+)?"
      + "repeat\\s+every\\s+([\\d\\.]+)\\s+(\\w+)\\s*"
      + "(after\\s+pause\\s+in\\s+([\\d\\.]+)\\s+(\\w+))?\\s*"
    ,
    Pattern.CASE_INSENSITIVE | Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
  );

  static ParseResult parseEng(String pattern) {
    return parseRegexp(ENG, pattern, 2, 3, 4, 6, 7);
  }

  private static ParseResult parseRegexp(Pattern regexp, String pattern, int parallelGroup,
                                         int repeatingByValueGroup, int repeatingByUnitGroup,
                                         int waitingForValueGroup, int waitingForUnitGroup) {
    final Matcher m = regexp.matcher(pattern);
    if (!m.matches()) return null;

    boolean parallel = m.group(parallelGroup) != null;
    long repeatingBy = readMillis(m.group(repeatingByValueGroup), m.group(repeatingByUnitGroup));
    long waitingFor = readMillis(m.group(waitingForValueGroup), m.group(waitingForUnitGroup));

    if (repeatingBy <= 0) throw new DelegateException("Illegal value of repeating by");

    return new ParseResult(parallel, repeatingBy, waitingFor);
  }

  private static long readMillis(String value, String unit) {
    if (value == null) return 0;
    return Math.round(Double.parseDouble(value)*millis(unit));
  }

  private static long millis(String unit) {
    String tmp = unit.toUpperCase();

    if (tmp.equals("С")) return 1000;
    if (tmp.equals("S")) return 1000;

    if (tmp.startsWith("SEC")) return 1000;
    if (tmp.startsWith("СЕК")) return 1000;

    if (tmp.startsWith("MIN")) return 60 * 1000;
    if (tmp.startsWith("МИН")) return 60 * 1000;

    throw new DelegateException("Unknown time unit: " + unit);
  }

}
