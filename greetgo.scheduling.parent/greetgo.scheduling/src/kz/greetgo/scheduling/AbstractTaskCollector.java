package kz.greetgo.scheduling;

import kz.greetgo.scheduling.context.SchedulerContext;
import kz.greetgo.scheduling.context.SchedulerContextSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public abstract class AbstractTaskCollector {

  public String defaultPoolName = "default";

  private final List<Task> tasks = new ArrayList<>();

  protected abstract SchedulerContext getSchedulerContext(Class<?> controllerClass);
  
  public List<Task> getTasks() {
    return unmodifiableList(tasks);
  }

  public void collect(Object controller) {
    final Class<?> controllerClass = controller.getClass();


    String topPoolName = defaultPoolName;
    final UsePool classUsePool = controllerClass.getAnnotation(UsePool.class);
    if (classUsePool != null) topPoolName = classUsePool.value().trim();

    SchedulerContext schedulerContext = getSchedulerContext(controllerClass);

    for (Method method : controllerClass.getMethods()) {
      final SchedulerMatcherTrigger smt = SchedulerMatcherTrigger.create(method, controller, schedulerContext);
      if (smt == null) continue;

      String poolName = topPoolName;
      final UsePool usePool = controllerClass.getAnnotation(UsePool.class);
      if (usePool != null) poolName = usePool.value();

      tasks.add(new Task(poolName, smt.job, smt, schedulerContext.throwableCatcher()));
    }
  }
}
