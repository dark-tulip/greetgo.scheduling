package kz.greetgo.scheduling;

public interface SchedulerMatcherDelegate {
  boolean match(long lastCheckTime, long now);

  boolean isParallel();

  void taskStartedAt(long taskStartedAt);

  void taskFinishedAt(long taskFinishedAt);
}
