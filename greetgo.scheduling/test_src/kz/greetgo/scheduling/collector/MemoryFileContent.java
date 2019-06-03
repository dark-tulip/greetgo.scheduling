package kz.greetgo.scheduling.collector;

import java.util.function.LongSupplier;

public class MemoryFileContent implements FileContent {

  private final LongSupplier currentTimeMillis;

  private String content;
  private long lastModifiedAt = 0;

  public MemoryFileContent(LongSupplier currentTimeMillis) {
    this.currentTimeMillis = currentTimeMillis;
  }

  @Override
  public String get() {
    return content;
  }

  @Override
  public void set(String value) {
    content = value;
    lastModifiedAt = currentTimeMillis.getAsLong();
  }

  @Override
  public long lastModifiedAt() {
    return lastModifiedAt;
  }

  @Override
  public boolean exists() {
    return content != null;
  }
}
