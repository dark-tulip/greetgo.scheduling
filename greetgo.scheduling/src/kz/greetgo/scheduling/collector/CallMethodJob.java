package kz.greetgo.scheduling.collector;

import java.lang.reflect.Method;

public class CallMethodJob implements Job {

  private final Object controller;
  private final Method method;

  private CallMethodJob(Object controller, Method method) {
    this.controller = controller;
    this.method = method;
  }

  public static CallMethodJob of(Object controller, Method method) {
    return new CallMethodJob(controller, method);
  }

  @Override
  public void execute() throws Throwable {

    try {

      method.invoke(controller);

    } catch (java.lang.reflect.InvocationTargetException e) {
      throw e.getCause();
    }

  }

}
