package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.trigger.TriggerParser;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

import static java.util.Objects.requireNonNull;

/**
 * <p>
 * Обеспечивает логику работы с файлами: конфигом и его ошибками - их чтение, создание, обновление, удаление
 * </p>
 * <p>
 * Этот класс используется только из одного потока
 * </p>
 */
public class ControllerContext {

  private final FileContent configFile;
  private final FileContent errorFile;
  private final String headerHelp;
  private final long checkFileDelayMillis;
  private final LongSupplier currentTimeMillis;

  public ControllerContext(FileContent configFile, FileContent errorFile,
                           String headerHelp, long checkFileDelayMillis,
                           LongSupplier currentTimeMillis) {

    requireNonNull(configFile, "configFile");
    requireNonNull(errorFile, "errorFile");
    requireNonNull(currentTimeMillis, "currentTimeMillis");
    this.configFile = configFile;
    this.errorFile = errorFile;
    this.headerHelp = headerHelp;
    this.checkFileDelayMillis = checkFileDelayMillis;
    this.currentTimeMillis = currentTimeMillis;

  }

  private long lastCheckTime = 0;
  private long lastGetModificationTime = 0;
  private List<String> lines = null;
  private Map<String, String> patterns = null;

  private final List<ScheduledDefinition> definitions = new ArrayList<>();
  private final Map<String, ScheduledDefinition> definitionMap = new HashMap<>();

  public void register(ScheduledDefinition definition) {
    definitions.add(definition);
    definitionMap.put(definition.name, definition);
  }

  private final Map<String, Trigger> triggers = new HashMap<>();

  public Trigger trigger(String name) {
    prepare();

    {
      Trigger trigger = triggers.get(name);
      if (trigger != null) {
        return trigger;
      }
    }
    {
      Trigger trigger = TriggerParser.parse(patterns.get(name)).trigger();
      triggers.put(name, trigger);
      return trigger;
    }
  }

  private void prepare() {

  }

}
