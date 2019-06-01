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


  private static String mul(String s, int times) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < times; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  public static void printErrors(String source, List<ParseError> errorList) {
    for (ParseError err : errorList) {
      System.err.println("Ошибка   : " + err.errorCode + " " + err.message + " : `" + err.range.cut(source) + "`");
      System.err.println("  Строка : " + source);
      String s1 = mul(" ", err.range.from);
      String s2 = mul("¯", err.range.to - err.range.from);//"‾¯"
      System.err.println("           " + s1 + s2);
    }
  }

  @DataProvider
  Object[][] repeat_from_to_DP() {
    return new Object[][]{
      {"повторять каждые 10 минут   с    10:11 до 11:45:23"},
      {"repeat    every  10 minutes from 10:11 to 11:45:23"},
    };
  }

  @Test(dataProvider = "repeat_from_to_DP")
  public void repeat_from_to(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isTrue();
    assertThat(trigger.toString()).isEqualTo("(Repeat{0 600000} and PeriodInDay{10:11:00...11:45:23})");

  }

  @DataProvider
  Object[][] repeat_from_to_twice_DP() {
    return new Object[][]{
      {"повторять каждую 1 минуту с    13:31 до 15:00  от   23:30 до 23:55"},
      {"repeat    every  1 minute from 13:31 to 15:00  from 23:30 to 23:55"},
    };
  }

  @Test(dataProvider = "repeat_from_to_twice_DP")
  public void repeat_from_to_twice(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isTrue();
    assertThat(trigger.toString()).isEqualTo(
      "(Repeat{0 60000} and (PeriodInDay{13:31:00...15:00:00} or PeriodInDay{23:30:00...23:55:00}))"
    );

  }

  @DataProvider
  Object[][] repeat_from_to_triple_DP() {
    return new Object[][]{
      {"повторять каждые 13 секунд с    11:00 до 13:00  от   14:30 до 15:55  с    18:00 до 19:35"},
      {"repeat    every  13 sec    from 11:00 to 13:00  from 14:30 to 15:55  from 18:00 to 19:35"},
    };
  }

  @Test(dataProvider = "repeat_from_to_triple_DP")
  public void repeat_from_to_triple(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isTrue();
    assertThat(trigger.toString()).isEqualTo(
      "(Repeat{0 13000} and ("

        + "(PeriodInDay{11:00:00...13:00:00} or PeriodInDay{14:30:00...15:55:00}) or PeriodInDay{18:00:00...19:35:00})"

        + ")"
    );

  }


}
