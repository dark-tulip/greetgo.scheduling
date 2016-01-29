package kz.greetgo.scheduling;

/**
 * Интерфейс задачи
 */
public interface Job {
  /**
   * Выполняет работу
   *
   * @throws Throwable выбрасывается, если задача завершилась с ошибкой
   */
  void doWork() throws Throwable;

  /**
   * Должен возвращать информацию о таске, чтобы понятно было где она создана. Используется в сообщениях об ошибках
   *
   * @return информация о таске
   */
  String infoForError();
}
