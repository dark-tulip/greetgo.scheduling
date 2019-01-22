package kz.greetgo.scheduling;

public class SchedulerMatcher {

  private final SchedulerMatcherDelegate delegate;

  public SchedulerMatcher(String pattern, String place, TaskRunStatus taskRunStatus) {
    try {
      delegate = create(pattern, taskRunStatus, place);
    } catch (DelegateException e) {
      throw new LeftSchedulerPattern(e.getMessage(), pattern, place);
    }
  }

  private static SchedulerMatcherDelegate create(String pattern, TaskRunStatus taskRunStatus, String place) {
    {
      final SchedulerMatcherDisabled smd = SchedulerMatcherDisabled.parse(pattern, taskRunStatus);
      if (smd != null) {
        return smd;
      }
    }

    {
      final SchedulerMatcherDelegate smd = SchedulerMatcherRepeat.parse(pattern, taskRunStatus);
      if (smd != null) {
        return smd;
      }
    }

    return new SchedulerMatcherCalendar(pattern, place);
  }

  public boolean match(long lastCheckTime, long now) {
    return delegate.match(lastCheckTime, now);
  }

  public boolean isParallel() {
    return delegate.isParallel();
  }

  public void taskStartedAt(long taskStartedAt) {
    delegate.taskStartedAt(taskStartedAt);
  }

  public void taskFinishedAt(long taskFinishedAt) {
    delegate.taskFinishedAt(taskFinishedAt);
  }

  public void taskFellInExecutionQueueAt(long taskFellInExecutionQueueAt) {
    delegate.taskFellInExecutionQueueAt(taskFellInExecutionQueueAt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SchedulerMatcher that = (SchedulerMatcher) o;

    return delegate.equals(that.delegate);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
