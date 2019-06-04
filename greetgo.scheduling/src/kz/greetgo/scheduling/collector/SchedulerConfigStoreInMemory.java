package kz.greetgo.scheduling.collector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SchedulerConfigStoreInMemory implements SchedulerConfigStore {

  private final Supplier<Long> currentTimeMillis;

  public SchedulerConfigStoreInMemory(Supplier<Long> currentTimeMillis) {
    this.currentTimeMillis = currentTimeMillis;
  }

  private final Map<String, String> content = new HashMap<>();
  private final Map<String, Long> contentModification = new HashMap<>();

  public void clear() {
    content.clear();
    contentModification.clear();
  }

  @Override
  public boolean exists(String location) {
    return content.containsKey(location);
  }

  @Override
  public String getContent(String location) {
    return content.get(location);
  }

  @Override
  public void setContent(String location, String content) {
    this.content.put(location, content);
    contentModification.put(location, currentTimeMillis.get());
  }

  @Override
  public long lastModifiedMillis(String location) {
    Long ret = contentModification.get(location);
    return ret == null ? 0 : ret;
  }

  @Override
  public String placeInfo(String location) {
    return "MEM{" + location + "}";
  }

}
