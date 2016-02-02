package kz.greetgo.scheduling.annotations;

import java.lang.annotation.*;

/**
 * Указывает какой пул нужно использовать для запуска задач у этого класса
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UsePool {
  /**
   * Название используемого пула
   */
  String value();
}
