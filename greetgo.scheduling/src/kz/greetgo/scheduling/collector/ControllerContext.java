package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.trigger.inner_logic.Trigger;

import java.util.Objects;
import java.util.function.LongSupplier;

public class ControllerContext {

  private final FileContent configFile;
  private final FileContent errorFile;
  private final String headerHelp;
  private final long checkFileDelayMillis;
  private final LongSupplier currentTimeMillis;

  public ControllerContext(ControllerConfigStore ccs,
                           String headerHelp,
                           long checkFileDelayMillis,
                           LongSupplier currentTimeMillis) {
    Objects.requireNonNull(ccs, "controllerConfigStore");
    Objects.requireNonNull(currentTimeMillis, "currentTimeMillis");
    configFile = new FileContentBridge(ccs.schedulerConfigStore(), ccs.configLocation());
    errorFile = new FileContentBridge(ccs.schedulerConfigStore(), ccs.configErrorLocation());
    this.headerHelp = headerHelp;
    this.checkFileDelayMillis = checkFileDelayMillis;
    this.currentTimeMillis = currentTimeMillis;
  }

  public Trigger trigger(ScheduledDefinition definition) {
    return null;
  }

}
