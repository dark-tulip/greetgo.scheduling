package kz.greetgo.scheduling.context;

import kz.greetgo.util.ServerUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ContentStoreInFile implements ContentStore {

  private final File file;

  public ContentStoreInFile(File file) {
    if (file == null) throw new NullPointerException("file == null");
    this.file = file;
  }

  @Override
  public boolean exists() {
    return file.exists();
  }

  @Override
  public byte[] getContent() {
    try (FileInputStream in = new FileInputStream(file)) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ServerUtil.copyStreamsAndCloseIn(in, out);
      return out.toByteArray();
    } catch (FileNotFoundException e) {
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setContent(byte[] content) {
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    try (FileOutputStream out = new FileOutputStream(file)) {
      out.write(content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean delete() {
    return file.delete();
  }

  @Override
  public long lastModifiedMillis() {
    return file.lastModified();
  }

  @Override
  public String placeInfo() {
    return "File " + file.getName();
  }
}
