package kz.greetgo.scheduling.collector;

public class FileContentBridge implements FileContent {
  private final SchedulerConfigStore store;
  private final String location;

  public FileContentBridge(SchedulerConfigStore store, String location) {
    this.store = store;
    this.location = location;
  }

  @Override
  public String get() {
    return store.getContent(location);
  }

  @Override
  public void set(String value) {
    store.setContent(location, value);
  }

  @Override
  public long lastModifiedAt() {
    return store.lastModifiedMillis(location);
  }

  @Override
  public boolean exists() {
    return store.exists(location);
  }

}
