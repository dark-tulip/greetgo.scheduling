package kz.greetgo.scheduling.trigger.inner_logic;

public class ParseError extends RuntimeException {
  public final String errorCode;
  public final Range  range;
  public final String message;

  public ParseError(Range range, String errorCode, String message) {
    if (errorCode.length() != 7 && errorCode.length() != 10) {
      throw new IllegalArgumentException("errorCode.length must be 7 or 10. Actual length = " + errorCode.length());
    }
    this.errorCode = errorCode;
    this.range     = range;
    this.message   = message;
  }

  @Override
  public String getMessage() {
    return toString();
  }

  @Override
  public String toString() {
    return range + " : " + errorCode + " " + message;
  }
}
