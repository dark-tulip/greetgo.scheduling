package kz.greetgo.scheduling.collector;

public interface FileContent {
  String get();

  void set(String value);

  long lastModifiedAt();

  boolean exists();
}
