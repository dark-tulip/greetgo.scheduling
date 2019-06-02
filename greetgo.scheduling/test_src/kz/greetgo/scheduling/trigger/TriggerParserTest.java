package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static org.fest.assertions.api.Assertions.assertThat;

public class TriggerParserTest {

  @Test
  public void parse_ok() {

    String triggerString = " повторять каждые 30 минут * (понедельник + среда) * с 10:00 до 18:00 ";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    assertThat(errors).isEmpty();

    assertThat(result.errors()).isEmpty();
    assertThat(result.trigger().isParallel()).isFalse();
    assertThat(result.trigger().toString()).isEqualTo(
      "((Repeat{0 1800000} * (WeekDay{MONDAY} + WeekDay{WEDNESDAY})) * PeriodInDay{10:00:00...18:00:00})"
    );

  }

  @Test
  public void parse_someErrors() {

    String triggerString = " повторять каждые 30 минут look * (понедельник dsa + среда wow) * с 10:00 до  ";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("54327hs :: " + error);

  }

  @Test
  public void parse_parallel() {

    String triggerString = "паралельно повторять каждые 30 минут * (понедельник + среда) * с 10:00 до 18:00 ";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("gv543623 :: " + error);

    assertThat(errors).isEmpty();

    assertThat(result.errors()).isEmpty();
    assertThat(result.trigger().isParallel()).isTrue();
    assertThat(result.trigger().toString()).isEqualTo(
      "((Repeat{0 1800000} * (WeekDay{MONDAY} + WeekDay{WEDNESDAY})) * PeriodInDay{10:00:00...18:00:00})"
    );

  }

  @Test
  public void parse_parallel_error() {

    String triggerString = "паралельно повторять каждые 30 минут * (понедельник xxx + среда) * с 10:00 до 18:00 ";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("j6b542447 :: " + error);

    assertThat(errors).isNotEmpty();

    assertThat(result.errors()).isNotEmpty();
    assertThat(result.trigger().isParallel()).isTrue();
    assertThat(result.trigger().toString()).isEqualTo(
      "((Repeat{0 1800000} * (Silent + WeekDay{WEDNESDAY})) * PeriodInDay{10:00:00...18:00:00})"
    );

  }

}
