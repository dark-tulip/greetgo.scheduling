package kz.greetgo.scheduling;

public class SchedulerMatcher {

  final SchedulerMatcherDelegate delegate;

  public SchedulerMatcher(String pattern, long schedulerStartedAt, String place) {
    delegate = new SchedulerMatcherCalendar(pattern, schedulerStartedAt, place);
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
