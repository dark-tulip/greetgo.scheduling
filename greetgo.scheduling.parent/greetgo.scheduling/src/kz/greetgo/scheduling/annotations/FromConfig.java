package kz.greetgo.scheduling.annotations;

import java.lang.annotation.*;

/**
 * <p>
 * Указывает, что расписание запуска данного метода нужно читать из конфига. Имя конфига соответствует имени класса,
 * а имя параметра конфига равно имени запускаемого метода.
 * </p>
 * <p>
 * Данную аннотацию необходимо обязательно использовать параллельно с аннотацией {@link Scheduled}. Аннотация
 * {@link Scheduled} нужна для того, чтобы указать начальное значение при создании конфига
 * </p>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FromConfig {
  /**
   * Описание задачи - оно копируется в конфиг
   */
  String value();
}
