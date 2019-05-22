package kz.greetgo.scheduling.context;

public interface ContentStore {

  boolean exists();

  byte[] getContent();

  void setContent(byte[] content);

  @SuppressWarnings("UnusedReturnValue")
  boolean delete();

  long lastModifiedMillis();

  String placeInfo();
}
