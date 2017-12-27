package kz.greetgo.scheduling;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchedulerMatcherRepeat implements SchedulerMatcherDelegate {

  private final ParseResult parseResult;
  private final TaskRunStatus taskRunStatus;

  @Override
  public String toString() {
    return "" + parseResult;
  }

  private SchedulerMatcherRepeat(ParseResult parseResult, TaskRunStatus taskRunStatus) {
    this.parseResult = parseResult;
    this.taskRunStatus = taskRunStatus;
  }

  static class ParseResult {
    public final boolean parallel;
    public final long repeatingBy, waitingFor;

    ParseResult(boolean parallel, long repeatingBy, long waitingFor) {
      this.parallel = parallel;
      this.repeatingBy = repeatingBy;
      this.waitingFor = waitingFor;
    }

    @Override
    public String toString() {
      return "SchedulerMatcherRepeat: parallel " + parallel + ", repeatingBy " + repeatingBy
          + ", waitingFor " + waitingFor;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ParseResult that = (ParseResult) o;

      if (parallel != that.parallel) return false;
      if (repeatingBy != that.repeatingBy) return false;
      if (waitingFor != that.waitingFor) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = (parallel ? 1 : 0);
      result = 31 * result + (int) (repeatingBy ^ (repeatingBy >>> 32));
      result = 31 * result + (int) (waitingFor ^ (waitingFor >>> 32));
      return result;
    }
  }

  public static SchedulerMatcherDelegate parse(String pattern, TaskRunStatus taskRunStatus) {
    ParseResult parseResult = parseRus(pattern);
    if (parseResult == null) parseResult = parseEng(pattern);
    if (parseResult != null) return new SchedulerMatcherRepeat(parseResult, taskRunStatus);
    return null;
  }

  @Override
  public void taskFellInExecutionQueueAt(long taskFellInExecutionQueueAt) {
  }

  private long lastNowOnReturnTrue = 0;

  @Override
  public boolean match(long lastCheckTime, long now) {
//    System.out.println("taskStartedAt = " + taskStartedAt + ", taskFinishedAt = " + taskFinishedAt);
    if (!parseResult.parallel && taskRunStatus.inRuntimeCount.get() > 0) return false;

    long pause = parseResult.repeatingBy;
    if (lastNowOnReturnTrue == 0) pause = parseResult.waitingFor;

    long timeToStart = getBegin() + pause;

    if (now <= timeToStart) return false;

    lastNowOnReturnTrue = now;

    return true;
  }

  private long getBegin() {
    long begins = taskRunStatus.schedulerStartedAt.get();
    if (begins < lastNowOnReturnTrue) begins = lastNowOnReturnTrue;
    if (parseResult.parallel) return begins;
    {
      long lastFinishedAt = taskRunStatus.lastFinishedAt.get();
      if (lastFinishedAt > 0) return begins > lastFinishedAt ? begins : lastFinishedAt;
    }
    return begins;
  }

  @Override
  public boolean isParallel() {
    return parseResult.parallel;
  }

  @Override
  public void taskStartedAt(long taskStartedAt) {
  }

  @Override
  public void taskFinishedAt(long taskFinishedAt) {
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
    return Math.round(Double.parseDouble(value) * millis(unit));
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SchedulerMatcherRepeat that = (SchedulerMatcherRepeat) o;

    return parseResult.equals(that.parseResult);
  }

  @Override
  public int hashCode() {
    return parseResult.hashCode();
  }
}
