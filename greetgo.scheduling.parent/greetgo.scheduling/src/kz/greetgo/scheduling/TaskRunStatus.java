package kz.greetgo.scheduling;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TaskRunStatus {

  public final AtomicLong schedulerStartedAt = new AtomicLong(0);

  public final AtomicLong lastStartedAt = new AtomicLong(0);

  public final AtomicLong lastFinishedAt = new AtomicLong(0);

  public final AtomicInteger inRuntimeCount = new AtomicInteger(0);

  public void markStarted() {
    lastStartedAt.set(System.currentTimeMillis());
    inRuntimeCount.incrementAndGet();
  }

  public void markFinished() {
    lastFinishedAt.set(System.currentTimeMillis());
    inRuntimeCount.decrementAndGet();
  }
}
