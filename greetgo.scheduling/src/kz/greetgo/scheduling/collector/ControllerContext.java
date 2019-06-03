package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.trigger.TriggerParser;
import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Этот класс используется только из одного потока
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


  public void register(ScheduledDefinition definition) {

  }

  public Trigger trigger(String name) {
    return null;
  }

}
