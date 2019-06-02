package kz.greetgo.scheduling.collector;

import java.lang.reflect.Method;

public class CallMethodJob implements Job {

  private CallMethodJob() {
  }

  public static CallMethodJob of(Object controller, Method method) {
    return new CallMethodJob();
  }

  @Override
  public void execute() throws Throwable {

  }
}
