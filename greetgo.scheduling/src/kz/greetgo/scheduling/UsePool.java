package kz.greetgo.scheduling;

import java.lang.annotation.*;

/**
 * Specifies pool name to use for running of tasks
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsePool {
  /**
   * Using pool name
   */
  String value();
}
