package kz.greetgo.scheduling.collector;

public interface TriggerConfig {

  SchedulerConfigStore schedulerConfigStore();

  String configLocation();

  String configErrorLocation();

}
