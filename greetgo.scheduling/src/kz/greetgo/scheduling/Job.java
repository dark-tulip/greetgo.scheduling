package kz.greetgo.scheduling;

/**
 * Job of task
 */
public interface Job {
  /**
   * Making job
   *
   * @throws Throwable throws if task completes with error
   */
  void doWork() throws Throwable;

  /**
   * Returns task information to understand what this task is and where it placed is. It is used in error messages.
   *
   * @return Task information
   */
  String infoForError();
}
