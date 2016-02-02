package kz.greetgo.scheduling;

public class DelegateException extends RuntimeException {
  public DelegateException() {
    super();
  }

  public DelegateException(String message) {
    super(message);
  }

  public DelegateException(String message, Throwable cause) {
    super(message, cause);
  }

  public DelegateException(Throwable cause) {
    super(cause);
  }
}
