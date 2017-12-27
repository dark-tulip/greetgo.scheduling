package kz.greetgo.scheduling;

public class NoPoolWithName extends RuntimeException {
  public final String poolName;
  public final Task task;

  public NoPoolWithName(String poolName, Task task) {
    super("No pool with name " + poolName + " for task " + task.infoForError());
    this.poolName = poolName;
    this.task = task;
  }
}
