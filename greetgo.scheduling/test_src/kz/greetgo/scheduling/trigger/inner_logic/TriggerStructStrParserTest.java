package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.atoms.TriggerRepeat;
import kz.greetgo.scheduling.util.StrUtil;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


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


  public static void printErrors(String source, List<ParseError> errorList) {
    for (ParseError err : errorList) {
      System.err.println("Ошибка   : " + err.errorCode + " " + err.message + " : `" + err.range.cut(source) + "`");
      System.err.println("  Строка : " + source);
      String s1 = StrUtil.mul(" ", err.range.from);
      String s2 = StrUtil.mul("¯", err.range.to - err.range.from);//"‾¯"
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


  @DataProvider
  private Object[][] daysOfWeekDataProvider() {
    return new Object[][]{
      {1, "понедельник"}, {2, "вторник"}, {3, "среда"}, {4, "четверг"},
      {5, "пятница"}, {6, "суббота"}, {7, "воскресенье"},

      {1, "monday"}, {2, "tuesday"}, {3, "wednesday"}, {4, "thursday"},
      {5, "friday"}, {6, "saturday"}, {7, "sunday"},

      {1, "пн"}, {2, "вт"}, {3, "ср"}, {4, "чт"},
      {5, "пт"}, {6, "сб"}, {7, "вс"},

      {1, "mon"}, {2, "tue"}, {3, "wed"}, {4, "thu"},
      {5, "fri"}, {6, "sat"}, {7, "sun"},
    };
  }

  private String weekDayCode(int dayNumber) {
    switch (dayNumber) {
      default:
        throw new RuntimeException("7EPi6uc1on :: Unknown dayNumber = " + dayNumber);
      case 1:
        return "MONDAY";
      case 2:
        return "TUESDAY";
      case 3:
        return "WEDNESDAY";
      case 4:
        return "THURSDAY";
      case 5:
        return "FRIDAY";
      case 6:
        return "SATURDAY";
      case 7:
        return "SUNDAY";
    }
  }

  @Test(dataProvider = "daysOfWeekDataProvider")
  public void weekDay__manyVariants(int dayNumber, String source) {

    StringBuilder src = new StringBuilder();
    for (int i = 0, c = RND.plusInt(10); i < c; i++) {
      src.append(' ');
    }
    src.append(source);
    for (int i = 0, c = RND.plusInt(10); i < c; i++) {
      src.append(' ');
    }

    System.out.println("f5iPJ8KN1B :: src = [" + src + "]");

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), src.toString());

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isFalse();

    String weekDayCode = weekDayCode(dayNumber);

    assertThat(trigger.toString()).isEqualTo("WeekDay{" + weekDayCode + "}");
  }


  @DataProvider
  private Object[][] monthDataProvider() {
    return new Object[][]{
      {1, "январь"}, {2, "февраль"}, {3, "март"},
      {4, "апрель"}, {5, "май"}, {6, "июнь"},
      {7, "июль"}, {8, "август"}, {9, "сентябрь"},
      {10, "октябрь"}, {11, "ноябрь"}, {12, "декабрь"},

      {1, "january"}, {2, "february"}, {3, "march"},
      {4, "april"}, {5, "may"}, {6, "june"},
      {7, "july"}, {8, "august"}, {9, "september"},
      {10, "october"}, {11, "november"}, {12, "december"},

      {1, "янв"}, {2, "фев"}, {3, "мар"},
      {4, "апр"}, {5, "май"}, {6, "июн"},
      {7, "июл"}, {8, "авг"}, {9, "сен"},
      {10, "окт"}, {11, "ноя"}, {12, "дек"},

      {1, "jan"}, {2, "feb"}, {3, "mar"},
      {4, "apr"}, {5, "may"}, {6, "jun"},
      {7, "jul"}, {8, "aug"}, {9, "sep"},
      {10, "oct"}, {11, "nov"}, {12, "dec"},
    };
  }

  @Test(dataProvider = "monthDataProvider")
  public void monthOnly(int month, String source) {

    StringBuilder src = new StringBuilder();
    for (int i = 0, c = RND.plusInt(10); i < c; i++) {
      src.append(' ');
    }
    src.append(source);
    for (int i = 0, c = RND.plusInt(10); i < c; i++) {
      src.append(' ');
    }

    System.out.println("XbnZ6HRg1e :: src = [" + src + "]");

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), src.toString());

    Trigger trigger = parser.parse();

    printErrors(source, parser.errorList);

    assertThat(parser.errorList).isEmpty();
    assertThat(trigger).isNotNull();
    assertThat(trigger.isDotty()).isFalse();

    assertThat(trigger.toString()).isEqualTo("Month{" + month + "}");
  }

}
