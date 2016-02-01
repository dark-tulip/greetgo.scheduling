package kz.greetgo.scheduling;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class TaskCollector {
  private final Object controller;
  private final String configDir;
  private final List<Task> result = new ArrayList<>();

  private TaskCollector(Object controller, String configDir) {

    this.controller = controller;
    this.configDir = configDir;
  }

  public static List<Task> collect(Object controller, String configDir) {
    TaskCollector collector = new TaskCollector(controller, configDir);

    collector.doCollect();

    return unmodifiableList(collector.result);
  }

  private void doCollect() {

  }
}
