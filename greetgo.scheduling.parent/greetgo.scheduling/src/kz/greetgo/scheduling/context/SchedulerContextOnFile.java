package kz.greetgo.scheduling.context;

import kz.greetgo.scheduling.ExceptionCatcher;
import kz.greetgo.scheduling.ThrowableCatcher;

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

  public ThrowableCatcher throwableCatcher = new ThrowableCatcher() {
    @Override
    public void catchThrowable(Throwable throwable) {
      throwable.printStackTrace();
    }
  };

  @Override
  public ExceptionCatcher exceptionCatcher() {
    return exceptionCatcher;
  }

  @Override
  public ThrowableCatcher throwableCatcher() {
    return throwableCatcher;
  }

  public void makeExceptionCatcherThroughThrowableCatcher() {
    exceptionCatcher = new ExceptionCatcher() {
      @Override
      public void catchException(Exception e) {
        throwableCatcher.catchThrowable(e);
      }
    };
  }
}