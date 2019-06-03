package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.trigger.TriggerParser;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
  private long savedLastModifiedTime = 0;
  private List<String> lines = null;
  private Map<String, String> patterns = null;
  private Map<String, Integer> lineNumbers = null;

  private final List<ScheduledDefinition> definitions = new ArrayList<>();

  public void register(ScheduledDefinition definition) {
    definitions.add(definition);
  }

  private Map<String, Trigger> triggers = null;

  public Trigger trigger(String name) {
    prepare();

    if (triggers == null) {
      triggers = new HashMap<>();
    }

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
    long now = currentTimeMillis.getAsLong();

    if (patterns == null) {
      loadContentOrCreateConfig(now);
      return;
    }

    if (lastCheckTime + checkFileDelayMillis > now) {
      return;
    }

    lastCheckTime = now;

    long lastModifiedAt = configFile.lastModifiedAt();

    if (lastModifiedAt == 0) {
      loadContentOrCreateConfig(now);
      return;
    }

    if (lastModifiedAt == savedLastModifiedTime) {
      return;
    }

    loadContentOrCreateConfig(now);

    savedLastModifiedTime = configFile.lastModifiedAt();

    updateErrorFile();
  }

  private void loadContentOrCreateConfig(long now) {
    triggers = null;
    String content = configFile.get();

    if (content == null) {
      createContent(now);
      saveContent(now);
      return;
    }

    setContent(content);


    checkAllDefinitions(now);

  }

  private void saveContent(long now) {
    configFile.set(getContent());
    lastCheckTime = now;
    savedLastModifiedTime = configFile.lastModifiedAt();
  }

  private void checkAllDefinitions(long now) {
    boolean added = false;

    for (ScheduledDefinition definition : definitions) {
      if (!patterns.containsKey(definition.name)) {
        if (!added) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
          String nowStr = sdf.format(new Date(now));

          lines.add("# Added at " + nowStr);
        }
        added = true;
        appendDefinition(lines, patterns, lineNumbers, definition);
      }
    }

    if (added) {
      saveContent(now);
    }
  }

  private void createContent(long now) {
    List<String> lines = new ArrayList<>();
    Map<String, String> patterns = new HashMap<>();
    Map<String, Integer> lineNumbers = new HashMap<>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    String nowStr = sdf.format(new Date(now));

    lines.add("# Created at " + nowStr);
    if (headerHelp != null) {
      for (String line : headerHelp.split("\n")) {
        lines.add("# " + line);
      }
      lines.add("");
    }

    for (ScheduledDefinition definition : definitions) {
      appendDefinition(lines, patterns, lineNumbers, definition);
    }

    this.lines = lines;
    this.patterns = patterns;
    this.lineNumbers = lineNumbers;

  }

  private static void appendDefinition(List<String> lines,
                                       Map<String, String> patterns,
                                       Map<String, Integer> lineNumbers,
                                       ScheduledDefinition definition) {
    lines.add("#");
    for (String line : definition.patternDescription.split("\n")) {
      lines.add("# " + line);
    }
    lines.add(definition.name + " = " + definition.patternFromAnnotation);
    patterns.put(definition.name, definition.patternFromAnnotation);
    lineNumbers.put(definition.name, lines.size());
    lines.add("");
  }

  private void setContent(String content) {
    Map<String, String> patterns = new HashMap<>();
    List<String> lines = new ArrayList<>();

    for (String line : content.split("\n")) {
      lines.add(line);
      if (line.trim().startsWith("#")) {
        continue;
      }
      int idx = line.indexOf('#');
      if (idx < 0) {
        continue;
      }
      patterns.put(line.substring(0, idx).trim(), line.substring(idx + 1));
    }

    this.patterns = patterns;
    this.lines = lines;
  }

  private String getContent() {
    List<String> lines = this.lines;
    return lines == null ? null : String.join("\n", lines);
  }

  private void updateErrorFile() {
    ...........
  }
}
