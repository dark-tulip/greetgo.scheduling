package kz.greetgo.scheduling.collector;

import java.util.function.LongSupplier;

public class MemoryFileContent implements FileContent {

  private final LongSupplier currentTimeMillis;

  public String content;
  public long lastModifiedAt = 0;

  public boolean lastModifiedAtCalled;

  public MemoryFileContent(LongSupplier currentTimeMillis) {
    this.currentTimeMillis = currentTimeMillis;
  }

  public boolean getCalled;

  @Override
  public String get() {
    getCalled = true;
    return content;
  }

  @Override
  public void set(String value) {
    content = value;
    lastModifiedAt = currentTimeMillis.getAsLong();
  }

  @Override
  public long lastModifiedAt() {
    lastModifiedAtCalled = true;
    return lastModifiedAt;
  }

  @Override
  public boolean exists() {
    return content != null;
  }
}
