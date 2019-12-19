package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

  @Test
  public void parse_parallel_error2() {

    String triggerString = " repeat every 5 sec * (mon + wed zzz1)";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("j6b542447 :: " + error);

    assertThat(errors).isNotEmpty();

  }

  @Test
  public void parse_special_001() throws ParseException {

    String triggerString = "from 8:00 to 18:00 every 30 min";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("j6b542447 :: " + error);

    System.out.println("result = " + result.trigger());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    long startedAt = sdf.parse("2019-12-19 01:00:00").getTime();
    long millis = sdf.parse("2019-12-19 08:00:00").getTime();

    boolean hit = result.trigger().isHit(startedAt, millis - 1, millis + 1);

    System.out.println("hit = " + hit);

  }

}
