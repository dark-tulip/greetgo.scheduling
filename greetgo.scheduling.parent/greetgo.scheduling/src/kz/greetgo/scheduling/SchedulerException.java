package kz.greetgo.scheduling;

public class SchedulerException extends RuntimeException {
  public SchedulerException() {
    super();
  }

  public SchedulerException(String message) {
    super(message);
  }

  public SchedulerException(String message, Throwable cause) {
    super(message, cause);
  }

  public SchedulerException(Throwable cause) {
    super(cause);
  }
}
