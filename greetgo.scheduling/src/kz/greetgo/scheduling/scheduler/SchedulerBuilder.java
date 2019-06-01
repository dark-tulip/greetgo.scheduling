package kz.greetgo.scheduling.scheduler;

import kz.greetgo.scheduling.collector.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public class SchedulerBuilder {

  private ThrowCatcher throwCatcher = Throwable::printStackTrace;

  public SchedulerBuilder setThrowCatcher(ThrowCatcher throwCatcher) {
    Objects.requireNonNull(throwCatcher);
    this.throwCatcher = throwCatcher;
    return this;
  }

  public int defaultExecutionPoolSize = 170;

  private long pingDelayMillis = 200;

  public SchedulerBuilder setPingDelayMillis(int pingDelayMillis) {
    if (pingDelayMillis < 0) {
      throw new IllegalArgumentException("pingDelayMillis cannot be negative");
    }
    this.pingDelayMillis = pingDelayMillis;
    return this;
  }

  public SchedulerBuilder setDefaultExecutionPoolSize(int defaultExecutionPoolSize) {
    if (defaultExecutionPoolSize < 1) {
      throw new IllegalArgumentException("ExecutionPoolSize can be 1 or more");
    }
    this.defaultExecutionPoolSize = defaultExecutionPoolSize;
    return this;
  }

  private final Map<String, Integer> executionPoolSizeMap = new HashMap<>();

  public SchedulerBuilder setExecutionPoolSize(String executionPoolName, int size) {
    executionPoolSizeMap.put(executionPoolName, size);
    return this;
  }

  public int getExecutionPoolSize(String executionPoolName) {
    Integer size = executionPoolSizeMap.get(executionPoolName);
    return size == null ? defaultExecutionPoolSize : size;
  }

  public List<Task> taskList = new ArrayList<>();

  public SchedulerBuilder addTask(Task task) {
    taskList.add(task);
    return this;
  }

  public SchedulerBuilder addTasks(Collection<Task> task) {
    taskList.addAll(task);
    return this;
  }

  public Scheduler build() {

    Map<String, TaskHolder> taskHolderMap = new HashMap<>();
    Set<String> executionNameSet = new HashSet<>();

    for (Task task : taskList) {
      if (taskHolderMap.containsKey(task.id())) {
        throw new RuntimeException("Duplicate task id = `" + task.id() + "`");
      }
      executionNameSet.add(task.executionPoolName());
      taskHolderMap.put(task.id(), new TaskHolder(task, throwCatcher));
    }

    Map<String, ExecutionPool> executionPoolMap = executionNameSet
      .stream()
      .collect(toMap(name -> name, name -> new ExecutionPool(name, getExecutionPoolSize(name))));

    return new Scheduler(new ArrayList<>(taskHolderMap.values()), executionPoolMap, pingDelayMillis);

  }

}
