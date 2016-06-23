package kz.greetgo.scheduling.context;

public interface ContentStore {

  boolean exists();

  byte[] getContent();

  void setContent(byte[] content);

  boolean delete();

  long lastModifiedMillis();
  
  String placeInfo();
}
