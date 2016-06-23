package kz.greetgo.scheduling.context;

import java.io.File;

public class SchedulerContextSourceOnDir implements SchedulerContextSource {

  public String configExtension = ".class.scheduler";

  private final String configDir;

  public SchedulerContextSourceOnDir(String configDir) {
    this.configDir = configDir;
  }

  @Override
  public SchedulerContext forControllerClass(Class<?> controllerClass) {
    return new SchedulerContextOnFile(new File(configDir + "/" + controllerClass.getSimpleName() + configExtension));
  }
}
