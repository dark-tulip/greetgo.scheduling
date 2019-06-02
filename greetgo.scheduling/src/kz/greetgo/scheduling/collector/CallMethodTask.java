package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.UsePool;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

public class CallMethodTask implements Task {

  private CallMethodTask() {
  }

  public static CallMethodTask of(String id, Trigger trigger, UsePool usePool, Job job) {
    return new CallMethodTask();
  }

  @Override
  public String id() {
    return null;
  }

  @Override
  public Trigger trigger() {
    return null;
  }

  @Override
  public Job job() {
    return null;
  }

  @Override
  public String executionPoolName() {
    return null;
  }
}
