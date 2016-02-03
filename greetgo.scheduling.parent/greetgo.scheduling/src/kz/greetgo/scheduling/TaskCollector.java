package kz.greetgo.scheduling;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class TaskCollector {
  public String configExtension = ".class.scheduler";
  public String defaultPoolName = "default";
  public ThrowableCatcher throwableCatcher = new ThrowableCatcher() {
    @Override
    public void catchThrowable(Throwable throwable) {
      throwable.printStackTrace();
    }
  };

  private final String configDir;
  private final List<Task> tasks = new ArrayList<>();

  public TaskCollector(String configDir) {
    this.configDir = configDir;
  }

  public List<Task> getTasks() {
    return unmodifiableList(tasks);
  }

  public void collect(Object controller) {
    final Class<?> controllerClass = controller.getClass();

    final File configFile = new File(configDir + "/" + controllerClass.getSimpleName() + configExtension);

    String topPoolName = defaultPoolName;
    final UsePool classUsePool = controllerClass.getAnnotation(UsePool.class);
    if (classUsePool != null) topPoolName = classUsePool.value().trim();

    for (Method method : controllerClass.getMethods()) {
      final SchedulerMatcherTrigger smt = SchedulerMatcherTrigger.create(method, controller, configFile);
      if (smt == null) continue;

      String poolName = topPoolName;
      final UsePool usePool = controllerClass.getAnnotation(UsePool.class);
      if (usePool != null) poolName = usePool.value();

      tasks.add(new Task(poolName, smt.job, smt, throwableCatcher));
    }
  }
}
