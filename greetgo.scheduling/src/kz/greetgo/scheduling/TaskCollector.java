package kz.greetgo.scheduling;

import kz.greetgo.scheduling.context.SchedulerContext;
import kz.greetgo.scheduling.context.SchedulerContextOnFile;

import java.io.File;

public class TaskCollector extends AbstractTaskCollector {
  public String configExtension = ".class.scheduler";

  public ThrowableCatcher throwableCatcher = new ThrowableCatcher() {
    @Override
    public void catchThrowable(Throwable throwable) {
      throwable.printStackTrace();
    }
  };

  @Override
  protected SchedulerContext getSchedulerContext(Class<?> controllerClass) {
    SchedulerContextOnFile ret = new SchedulerContextOnFile(new File(
        configDir + "/" + controllerClass.getSimpleName() + configExtension
    ));
    ret.throwableCatcher = throwableCatcher;
    ret.makeExceptionCatcherThroughThrowableCatcher();
    return ret;
  }

  private final String configDir;

  public TaskCollector(String configDir) {
    this.configDir = configDir;
  }
}
