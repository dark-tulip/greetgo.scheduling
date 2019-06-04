package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.UsePool;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import static kz.greetgo.scheduling.scheduler.ExecutionPool.DEF_POOL_NAME;

public class CallMethodTask implements Task {

  private final String id;
  private final Trigger trigger;
  private final UsePool usePool;
  private final Job job;

  private CallMethodTask(String id, Trigger trigger, UsePool usePool, Job job) {
    this.id = id;
    this.trigger = trigger;
    this.usePool = usePool;
    this.job = job;
  }

  public static CallMethodTask of(String id, Trigger trigger, UsePool usePool, Job job) {
    return new CallMethodTask(id, trigger, usePool, job);
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public Trigger trigger() {
    return trigger;
  }

  @Override
  public Job job() {
    return job;
  }

  @Override
  public String executionPoolName() {
    return usePool == null ? DEF_POOL_NAME : usePool.value();
  }

}
