package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

public interface Task {

  String id();

  Trigger trigger();

  Job job();

  String executionPoolName();

}
