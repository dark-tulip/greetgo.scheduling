package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.UsePool;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static kz.greetgo.scheduling.util.StrUtil.streamToStr;

public class TaskCollector {

  private final String headerHelp;

  private TaskCollector() {
    headerHelp = streamToStr(getClass().getResourceAsStream("scheduler-help.txt"));
  }

  public static TaskCollector create() {
    return new TaskCollector();
  }

  private SchedulerConfigStore schedulerConfigStore;

  public TaskCollector setSchedulerConfigStore(SchedulerConfigStore schedulerConfigStore) {
    this.schedulerConfigStore = schedulerConfigStore;
    return this;
  }

  private long checkFileDelayMillis = 400;

  public TaskCollector setCheckFileDelayMillis(long checkFileDelayMillis) {
    this.checkFileDelayMillis = checkFileDelayMillis;
    return this;
  }

  private String configExtension = ".scheduler-config.txt";

  public TaskCollector setConfigExtension(String configExtension) {
    this.configExtension = configExtension;
    return this;
  }

  private String configErrorsExtension = ".scheduler-errors.txt";

  public TaskCollector setConfigErrorsExtension(String configErrorsExtension) {
    this.configErrorsExtension = configErrorsExtension;
    return this;
  }

  private final TriggerConfigSource triggerConfigSource = new TriggerConfigSource() {

    @Override
    public SchedulerConfigStore schedulerConfigStore() {
      SchedulerConfigStore ret = schedulerConfigStore;
      if (ret == null) {
        throw new RuntimeException("schedulerConfigStore is not defined");
      }
      return ret;
    }

    @Override
    public String configExtension() {
      String ret = configExtension;
      if (ret == null) {
        throw new RuntimeException("configExtension is not defined");
      }
      return ret;
    }

    @Override
    public String configErrorsExtension() {
      String ret = configErrorsExtension;
      if (ret == null) {
        throw new RuntimeException("configErrorsExtension is not defined");
      }
      return ret;
    }

  };

  private final List<Task> taskList = new ArrayList<>();

  public TaskCollector addController(Object controller) {

    MethodTriggerConfig triggerConfig = new MethodTriggerConfig(triggerConfigSource, controller);

    for (Method method : controller.getClass().getMethods()) {

      Scheduled scheduled = method.getAnnotation(Scheduled.class);
      if (scheduled == null) {
        continue;
      }

      String id = controller.getClass().getName() + "#" + method.getName();
      Job job = CallMethodJob.of(controller, method);
      FromConfig fromConfig = method.getAnnotation(FromConfig.class);
      UsePool usePool = method.getAnnotation(UsePool.class);

      Trigger trigger = TriggerOverMethod.create(method.getName(),
        triggerConfig, scheduled, fromConfig, headerHelp,
        checkFileDelayMillis, System::currentTimeMillis
      );

      taskList.add(CallMethodTask.of(id, trigger, usePool, job));
    }

    return this;
  }

  public TaskCollector addControllers(Collection<Object> controllers) {
    controllers.forEach(this::addController);
    return this;
  }

  public List<Task> getTasks() {
    return Collections.unmodifiableList(taskList);
  }

}
