package kz.greetgo.scheduling.collector;

public class FileContent {
  private final SchedulerConfigStore store;
  private final String location;

  public FileContent(SchedulerConfigStore store, String location) {
    this.store = store;
    this.location = location;
  }

  public String get() {
    return store.getContent(location);
  }

  public void set(String value) {
    store.setContent(location, value);
  }

  public long lastModifiedAt() {
    return store.lastModifiedMillis(location);
  }

  public boolean exists() {
    return store.exists(location);
  }

}
