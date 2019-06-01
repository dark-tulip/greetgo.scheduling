package kz.greetgo.scheduling.trigger.inner_logic;

public interface Trigger {

  boolean isHit(long schedulerStartedAtMillis, long timeMillisFrom, long timeMillisTo);

  /**
   * Возвращает признак точечности триггера. Если триггер точечный, то он генерирует события в точках по времени
   * (например каждые 10 секунд). Если период не точечный (промежуточный), то он описывает промежутки времени
   * (например с 13:00 по 17:00)
   *
   * @return <code>true</code> - триггер точечный, иначе - промежуточный
   */
  boolean isDotty();

}
