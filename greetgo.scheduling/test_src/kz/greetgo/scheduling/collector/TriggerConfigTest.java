package kz.greetgo.scheduling.collector;

public class TriggerConfigTest implements TriggerConfig {

  private final TriggerConfigSource source;
  private final String locationPrefix;

  public TriggerConfigTest(TriggerConfigSource source, String locationPrefix) {
    this.source = source;
    this.locationPrefix = locationPrefix;
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
