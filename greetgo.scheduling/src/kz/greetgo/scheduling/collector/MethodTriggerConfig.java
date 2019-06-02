package kz.greetgo.scheduling.collector;

public class MethodTriggerConfig implements TriggerConfig {

  private final TriggerConfigSource source;
  private final String locationPrefix;

  public MethodTriggerConfig(TriggerConfigSource source, Object controller) {
    this.source = source;
    locationPrefix = controller.getClass().getSimpleName();
  }

  @Override
  public SchedulerConfigStore schedulerConfigStore() {
    return source.schedulerConfigStore();
  }

  @Override
  public String configLocation() {
    return locationPrefix + source.configExtension();
  }

  @Override
  public String configErrorLocation() {
    return locationPrefix + source.configErrorsExtension();
  }
}
