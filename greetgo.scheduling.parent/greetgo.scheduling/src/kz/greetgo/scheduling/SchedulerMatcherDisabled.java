package kz.greetgo.scheduling;

import java.util.regex.Pattern;

public class SchedulerMatcherDisabled implements SchedulerMatcherDelegate {

  public static SchedulerMatcherDisabled parse(String pattern, TaskRunStatus taskRunStatus) {
    if (isDisabled(pattern)) return new SchedulerMatcherDisabled();
    return null;
  }

  private static final Pattern DIS = Pattern.compile(
      "\\s*(выкл\\w+|откл\\w*|off)\\s+.*",
      Pattern.CASE_INSENSITIVE | Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
  );

  static boolean isDisabled(String pattern) {
    return DIS.matcher(pattern + ' ').matches();
  }

  @Override
  public boolean match(long lastCheckTime, long now) {
    return false;
  }

  @Override
  public boolean isParallel() {
    return false;
  }

  @Override
  public void taskStartedAt(long taskStartedAt) {
  }

  @Override
  public void taskFinishedAt(long taskFinishedAt) {
  }

  @Override
  public void taskFellInExecutionQueueAt(long taskFellInExecutionQueueAt) {
  }

  @Override
  public String toString() {
    return "DISABLED";
  }
}
