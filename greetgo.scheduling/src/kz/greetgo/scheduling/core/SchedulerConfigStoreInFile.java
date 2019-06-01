package kz.greetgo.scheduling.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SchedulerConfigStoreInFile implements SchedulerConfigStore {

  private final Path root;

  public SchedulerConfigStoreInFile(Path rootPath) {
    root = rootPath;
  }

  @Override
  public boolean exists(String location) {
    return Files.exists(root.resolve(location));
  }

  @Override
  public byte[] getContent(String location) {
    try {
      return Files.readAllBytes(root.resolve(location));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setContent(String location, byte[] content) {
    Path path = root.resolve(location);

    if (content == null) {
      try {
        Files.delete(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return;
    }

    //noinspection ResultOfMethodCallIgnored
    path.toFile().getParentFile().mkdirs();
    try {
      Files.write(path, content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public long lastModifiedMillis(String location) {
    try {
      return Files.getLastModifiedTime(root.resolve(location)).toMillis();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String placeInfo(String location) {
    return root.resolve(location).toString();
  }
}
