package kz.greetgo.scheduling.context;

public interface SchedulerContextSource {
  SchedulerContext forControllerClass(Class<?> controllerClass);
}
