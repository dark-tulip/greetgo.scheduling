package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.Objects;
import java.util.function.LongSupplier;

/**
 * Этот класс используется только из одного потока
 */
public class ControllerContext {

  private final FileContent configFile;
  private final FileContent errorFile;
  private final String headerHelp;
  private final long checkFileDelayMillis;
  private final LongSupplier currentTimeMillis;

  public ControllerContext(FileContent configFile,
                           FileContent errorFile,
                           String headerHelp,
                           long checkFileDelayMillis,
                           LongSupplier currentTimeMillis) {

    Objects.requireNonNull(configFile, "configFile");
    Objects.requireNonNull(errorFile, "errorFile");
    Objects.requireNonNull(currentTimeMillis, "currentTimeMillis");
    this.configFile = configFile;
    this.errorFile = errorFile;
    this.headerHelp = headerHelp;
    this.checkFileDelayMillis = checkFileDelayMillis;
    this.currentTimeMillis = currentTimeMillis;
  }

  public Trigger trigger(ScheduledDefinition definition) {
    return null;
  }

}
