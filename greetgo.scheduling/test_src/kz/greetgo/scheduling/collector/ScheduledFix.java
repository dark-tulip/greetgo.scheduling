package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.Scheduled;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public class ScheduledFix implements Scheduled {

  private final String value;

  public ScheduledFix(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return getClass();
  }

}
