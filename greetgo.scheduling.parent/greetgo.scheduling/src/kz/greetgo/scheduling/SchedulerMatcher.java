package kz.greetgo.scheduling;

public class SchedulerMatcher {

  private final SchedulerMatcherDelegate delegate;

  public SchedulerMatcher(String pattern, long schedulerStartedAt, String place) {
    try {
      delegate = create(pattern, schedulerStartedAt, place);
    } catch (DelegateException e) {
      throw new LeftSchedulerPattern(e.getMessage(), pattern, place);
    }
  }

  private SchedulerMatcherDelegate create(String pattern, long schedulerStartedAt, String place) {
    {
      final SchedulerMatcherDisabled smd = SchedulerMatcherDisabled.parse(pattern);
      if (smd != null) return smd;
    }

    {
      final SchedulerMatcherDelegate smd = SchedulerMatcherRepeat.parse(pattern, schedulerStartedAt);
      if (smd != null) return smd;
    }

    return new SchedulerMatcherCalendar(pattern, schedulerStartedAt, place);
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
}
