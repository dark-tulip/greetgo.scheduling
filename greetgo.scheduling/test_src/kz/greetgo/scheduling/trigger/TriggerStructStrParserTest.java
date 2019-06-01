package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.trigger.atoms.TriggerRepeat;
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

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isNotEmpty();
    ParseError error = parser.errorList.get(0);
    assertThat(error.errorCode).isEqualTo("2h4hY88");
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

    parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isNotEmpty();

  }

  @DataProvider
  Object[][] period_in_day_DP() {
    return new Object[][]{
      {"от   13:31 до 15:00"},
      {"from 13:31 to 15:00"},
    };
  }

  @Test(dataProvider = "period_in_day_DP")
  public void period_in_day(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isFalse();
    assertThat(trigger.toString()).isEqualTo("PeriodInDay{13:31:00...15:00:00}");

  }

  @DataProvider
  Object[][] weekDay_DP() {
    return new Object[][]{
      {" понедельник "},
      {" monday      "},
    };
  }

  @Test(dataProvider = "weekDay_DP")
  public void weekDay(String source) {
    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isFalse();
    assertThat(trigger.toString()).isEqualTo("WeekDay{MONDAY}");
  }

  @Test
  public void timeOfDayToMinutes() {

    String source = " 17:35 ";

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isTrue();
    assertThat(trigger.toString()).isEqualTo("DayPoint{17:35:00}");
  }

  @DataProvider
  Object[][] from_to_every_DP() {
    return new Object[][]{
      {" от   13:31 до 15:00 каждые 17 минут   "},
      {" from 13:31 to 15:00 every  17 minutes "},
    };
  }

  @Test(dataProvider = "from_to_every_DP")
  public void from_to_every(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isTrue();
    assertThat(trigger.toString()).isEqualTo(
      "PeriodInDayRepeat{PeriodInDay{13:31:00...15:00:00} repeat 1020000}"
    );

  }

  @DataProvider
  Object[][] from_to_every_error_DP() {
    return new Object[][]{
      {" от   13:31 до 15:00 каждые "},
      {" from 13:31 to 15:00 every  "},
    };
  }

  @Test(dataProvider = "from_to_every_error_DP")
  public void from_to_every_error(String source) {

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source);

    parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isNotEmpty();
    assertThat(parser.errorList.get(0).errorCode).isEqualTo("2135jh6");


  }

}
