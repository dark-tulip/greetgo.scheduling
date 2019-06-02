package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import kz.greetgo.scheduling.trigger.TriggerParser;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class TriggerOverMethod implements Trigger {

  private final TriggerConfig triggerConfig;
  private final Scheduled scheduled;
  private final FromConfig fromConfig;
  private final String configHelpHeader;
  private final long checkFileDelayMillis;
  private String methodName;
  private final Supplier<Long> currentTimeMillis;
  private final FileContent config;
  private final FileContent error;

  private TriggerOverMethod(String methodName,
                            TriggerConfig triggerConfig,
                            Scheduled scheduled,
                            FromConfig fromConfig,
                            String configHelpHeader,
                            long checkFileDelayMillis,
                            Supplier<Long> currentTimeMillis) {
    this.methodName = methodName;
    this.currentTimeMillis = currentTimeMillis;

    requireNonNull(triggerConfig, "triggerConfig");
    requireNonNull(scheduled, "scheduled");
    this.triggerConfig = triggerConfig;
    this.scheduled = scheduled;
    this.fromConfig = fromConfig;
    this.configHelpHeader = configHelpHeader;
    this.checkFileDelayMillis = checkFileDelayMillis;
    config = new FileContent(triggerConfig.schedulerConfigStore(), triggerConfig.configLocation());
    error = new FileContent(triggerConfig.schedulerConfigStore(), triggerConfig.configErrorLocation());

    if (fromConfig == null) {
      TriggerParseResult parseResult = TriggerParser.parse(scheduled.value());
      ScheduledParseException.check(parseResult.errors());
      trigger.set(parseResult.trigger());
    }

  }

  private final AtomicReference<Trigger> trigger = new AtomicReference<>(null);

  public static TriggerOverMethod create(String methodName, TriggerConfig triggerConfig, Scheduled scheduled,
                                         FromConfig fromConfig, String configHelpHeader,
                                         long checkFileDelayMillis, Supplier<Long> currentTimeMillis) {

    return new TriggerOverMethod(

      methodName, triggerConfig, scheduled, fromConfig, configHelpHeader, checkFileDelayMillis, currentTimeMillis

    );

  }

  @Override
  public boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo) {
    ping();
    return trigger.get().isHit(schedulerStartedAtMillis, timeMillisFrom, timeMillisTo);
  }

  @Override
  public boolean isDotty() {
    ping();
    return true;
  }

  @Override
  public boolean isParallel() {
    ping();
    return trigger.get().isParallel();
  }

  @Override
  public String toString() {
    return "Wrapper[" + trigger.get() + "]";
  }

  private final AtomicLong lastFileCheckedAt = new AtomicLong(0);
  private final AtomicLong lastFileModifiedAt = new AtomicLong(0);

  private void ping() {
    if (fromConfig == null) {
      return;
    }

    long lastConfigModifiedAt = this.lastFileModifiedAt.get();

    if (lastConfigModifiedAt == 0) {

      lastFileCheckedAt.set(currentTimeMillis.get());

      if (!config.exists()) {
        createConfigOrErrorFiles();
        return;
      }

      this.lastFileModifiedAt.set(config.lastModifiedAt());
      readConfigAndWriteErrorOrAppendToEnd();

      return;
    }

    long now = currentTimeMillis.get();
    long lastCheckedAt = lastFileCheckedAt.get();

    if (lastCheckedAt + checkFileDelayMillis > now) {
      return;
    }

    long justReadConfigModifiedAt = config.lastModifiedAt();
    lastFileCheckedAt.set(now);
    if (lastConfigModifiedAt == justReadConfigModifiedAt) {
      return;
    }

    readConfigAndWriteErrorOrAppendToEnd();
  }

  private void readConfigAndWriteErrorOrAppendToEnd() {
    String schedulerString = readFromFile();
  }

  private String readFromFile() {

    String content = config.get();
    if (content == null) {
      return null;
    }

    for (String line : content.split("\n")) {
      String trimmedLine = line.trim();
      if (trimmedLine.startsWith("#")) {
        continue;
      }
      int idx = line.indexOf('=');
      if (idx < 0) {
        continue;
      }

      .........
    }

    return null;
  }

  private void createConfigOrErrorFiles() {

    StringBuilder sb = new StringBuilder();

    String configHelpHeader = this.configHelpHeader;
    if (configHelpHeader != null) {
      for (String line : configHelpHeader.split("\n")) {
        sb.append("# ").append(line).append("\n");
      }
    }

    sb.append("\n");

    sb.append("#\n");
    for (String line : fromConfig.value().split("\n")) {
      sb.append("# ").append(line).append("\n");
    }

    sb.append(methodName).append(" = ").append(scheduled.value());

    config.set(sb.toString());
    lastFileModifiedAt.set(config.lastModifiedAt());

    checkOnErrorAndCreateOrDeleteErrorFile();

  }

  private void checkOnErrorAndCreateOrDeleteErrorFile() {

  }

}
