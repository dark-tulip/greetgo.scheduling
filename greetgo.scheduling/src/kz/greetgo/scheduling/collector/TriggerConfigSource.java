package kz.greetgo.scheduling.collector;

public interface TriggerConfigSource {

  SchedulerConfigStore schedulerConfigStore();

  String configExtension();

  String configErrorsExtension();

}
