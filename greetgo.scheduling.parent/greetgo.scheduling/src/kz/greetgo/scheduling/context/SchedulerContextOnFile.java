package kz.greetgo.scheduling.context;

import kz.greetgo.scheduling.ExceptionCatcher;

import java.io.File;

public class SchedulerContextOnFile implements SchedulerContext {

  private final ContentStore configContent, errorContent;

  public SchedulerContextOnFile(File file) {
    configContent = new ContentStoreInFile(file);
    errorContent = new ContentStoreInFile(new File(file.getPath() + ".error"));
  }

  @Override
  public ContentStore configContent() {
    return configContent;
  }

  @Override
  public ContentStore configError() {
    return errorContent;
  }

  public ExceptionCatcher exceptionCatcher = null;

  @Override
  public ExceptionCatcher exceptionCatcher() {
    return exceptionCatcher;
  }
}
