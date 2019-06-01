package kz.greetgo.scheduling.exceptions;

import kz.greetgo.scheduling.trigger.TriggerParseError;

import java.util.List;
import java.util.Optional;

import static kz.greetgo.scheduling.util.StrUtil.mul;

public class ScheduledParseException extends RuntimeException {

  public ScheduledParseException(String message) {
    super(message);
  }

  public static Optional<ScheduledParseException> exception(List<TriggerParseError> errors) {
    return generateErrorMessage(errors).map(ScheduledParseException::new);
  }

  public static void check(List<TriggerParseError> errors) {
    generateErrorMessage(errors)
      .map(ScheduledParseException::new)
      .ifPresent(e -> { throw e; });
  }

  public static Optional<String> generateErrorMessage(List<TriggerParseError> errors) {
    if (errors.isEmpty()) {
      return Optional.empty();
    }

    StringBuilder sb = new StringBuilder();
    sb.append("\n");

    for (TriggerParseError error : errors) {

      sb.append("Ошибка  : ").append(error.errorCode()).append(" ").append(error.errorMessage()).append("\n");
      sb.append("  Строка: ").append(error.triggerString()).append("\n");
      String s1 = mul(" ", error.errorPlace().from);
      String s2 = mul("¯", error.errorPlace().to - error.errorPlace().from);//"‾¯"
      sb.append("          ").append(s1).append(s2).append("\n");

    }

    return Optional.of(sb.toString());
  }

}
