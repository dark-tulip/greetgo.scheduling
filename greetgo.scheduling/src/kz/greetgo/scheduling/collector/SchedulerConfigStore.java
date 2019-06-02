package kz.greetgo.scheduling.collector;

public interface SchedulerConfigStore {

  boolean exists(String location);

  String getContent(String location);

  void setContent(String location, String content);

  default void delete(String location) {
    setContent(location, null);
  }

  long lastModifiedMillis(String location);

  String placeInfo(String location);

}
