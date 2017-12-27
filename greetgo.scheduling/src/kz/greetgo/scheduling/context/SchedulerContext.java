package kz.greetgo.scheduling.context;

import kz.greetgo.scheduling.ExceptionCatcher;
import kz.greetgo.scheduling.ThrowableCatcher;
import kz.greetgo.scheduling.context.ContentStore;

public interface SchedulerContext {
  ContentStore configContent();

  ContentStore configError();

  ExceptionCatcher exceptionCatcher();

  ThrowableCatcher throwableCatcher();
  
  String machineId();
}
