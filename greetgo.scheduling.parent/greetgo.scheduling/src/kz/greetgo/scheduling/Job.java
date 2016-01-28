package kz.greetgo.scheduling;

public interface Job {
  void doWork() throws Throwable;

  /**
   * Должен возвращать информацию о таске, чтобы понятно было где она создана. Используется в сообщениях об ошибках
   *
   * @return информация о таске
   */
  String infoForError();
}
