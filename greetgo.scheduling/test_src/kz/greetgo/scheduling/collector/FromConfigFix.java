package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.FromConfig;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public class FromConfigFix implements FromConfig {

  private final String value;

  public FromConfigFix(String value) {
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
