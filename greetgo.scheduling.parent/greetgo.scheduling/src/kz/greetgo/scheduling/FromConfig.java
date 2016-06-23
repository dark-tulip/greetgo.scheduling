package kz.greetgo.scheduling;

import java.lang.annotation.*;

/**
 * <p>
 * Indicates that timing string has been taking from config place
 * </p>
 * <p>
 * This annotation is using only with annotation {@link Scheduled}. In this case annotation {@link Scheduled} inform
 * about started value of timing? placed in config place.
 * </p>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FromConfig {
  /**
   * Task description - it is copied in to config place
   */
  String value();
}
