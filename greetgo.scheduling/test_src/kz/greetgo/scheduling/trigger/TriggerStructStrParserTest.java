package kz.greetgo.scheduling.trigger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class TriggerStructStrParserTest {

  @DataProvider
  Object[][] repeat_every_10_seconds_DP() {
    return new Object[][]{
      {"повторять каждые 13 секунд"},
      {"repeat every 13 seconds"},
    };
  }

  @Test(dataProvider = "repeat_every_10_seconds_DP")
  public void repeat_every_10_seconds(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger).isInstanceOf(TriggerRepeat.class);
    assertThat(trigger.toString()).isEqualTo("Repeat{0 13000}");
  }

  @DataProvider
  Object[][] repeat_every_10_seconds_after_pause_17_minutes_DP() {
    return new Object[][]{
      {"повторять каждые 10 секунд начиная с паузы 17 минут"},
      {"repeat every 10 seconds after pause in 17 minutes"},
    };
  }

  @Test(dataProvider = "repeat_every_10_seconds_after_pause_17_minutes_DP")
  public void repeat_every_10_seconds_after_pause_17_minutes(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger).isInstanceOf(TriggerRepeat.class);
    assertThat(trigger.toString()).isEqualTo("Repeat{1020000 10000}");

  }

  @Test
  public void repeat_with_error1() {

    String source = "repeat every 19 minutes at ";

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    parser.parse();

    assertThat(parser.errorList).isNotEmpty();
    ParseError error = parser.errorList.get(0);
    assertThat(error.errorCode).isEqualTo("j25bhj4");
    assertThat(error.range.cut(source)).isEqualTo("at");

  }

  @Test
  public void repeat_with_error2() {

    String source = "repeat every 19 minutes wer ";

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    parser.parse();

    assertThat(parser.errorList).isNotEmpty();
    ParseError error = parser.errorList.get(0);
    assertThat(error.errorCode).isEqualTo("26kjb43");
    assertThat(error.range.cut(source)).isEqualTo("wer");

  }


  @DataProvider
  Object[][] repeat_from_to_DP() {
    return new Object[][]{
      {"повторять каждые 10 минут с 10:11 до 11:45:23"},
      {"repeat every 10 minutes from 10:11 to 11:45:23"},
    };
  }

  public static void printErrors(String source, List<ParseError> errorList) {
    for (ParseError err : errorList) {
      System.out.println("ERR " + err + " :: `" + err.range.cut(source) + "`");
    }
  }

  @Test(dataProvider = "repeat_from_to_DP")
  public void repeat_from_to(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    System.out.println("5kb4h26 :: trigger = " + trigger);
    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(1).isNotNull();


  }


}
