package kz.greetgo.scheduling.collector;

public interface SchedulerConfigStore {

  boolean exists(String location);

  byte[] getContent(String location);

  void setContent(String location, byte[] content);

  default void delete(String location) {
    setContent(location, null);
  }

  long lastModifiedMillis(String location);

  String placeInfo(String location);

}
