package kz.greetgo.scheduling.collector;

public interface SchedulerConfigStoreWithExtensions {

  SchedulerConfigStore schedulerConfigStore();

  String configExtension();

  String configErrorsExtension();

}
