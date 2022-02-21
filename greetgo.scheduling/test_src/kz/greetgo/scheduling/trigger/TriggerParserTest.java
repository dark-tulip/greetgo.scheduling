package kz.greetgo.scheduling.trigger;

import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerParseResult;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

    String triggerString = "параллельно повторять каждые 30 минут * (понедельник + среда) * с 10:00 до 18:00 ";

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

    String triggerString = "параллельно повторять каждые 30 минут * (понедельник xxx + среда) * с 10:00 до 18:00 ";

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
    long millis    = sdf.parse("2019-12-19 08:00:00").getTime();

    boolean hit = result.trigger().isHit(startedAt, millis - 1, millis + 1);

    System.out.println("hit = " + hit);

    assertThat(hit).isTrue();

  }

  @Test
  public void parse_special_002() throws ParseException {

    String triggerString = "8:00";

    //
    //
    TriggerParseResult result = TriggerParser.parse(triggerString);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("df3qw3414 :: " + error);

    System.out.println("result = " + result.trigger());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    long startedAt = sdf.parse("2019-12-19 01:00:00").getTime();
    long millis    = sdf.parse("2019-12-19 08:00:00").getTime();

    boolean hit = result.trigger().isHit(startedAt, millis - 1, millis + 1);

    System.out.println("hit = " + hit);

    assertThat(hit).isTrue();

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

  @Test(dataProvider = "daysOfWeekDataProvider")
  public void parse_dayOfWeek(int dayNumber, String dayName) {

    //
    //
    TriggerParseResult result = TriggerParser.parse("11:00 * " + dayName);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("9eWm4PIf2I :: " + error);

    System.out.println("u1aG50dJ0q :: result = " + result.trigger());

    Calendar calendar = new GregorianCalendar();
    calendar.setTime(RND.dateYears(-2, 0));
    calendar.set(Calendar.DAY_OF_WEEK, dayNumber + 1);
    calendar.set(Calendar.HOUR_OF_DAY, 11);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    System.out.println("o3FYIBe1v8 :: calendar " + calendar.getTime()
                         + ", dayNumber = " + dayNumber
                         + ", dayName = " + dayName);

    long millis = calendar.getTimeInMillis();
    calendar.add(Calendar.HOUR, -3);
    long startedAt = calendar.getTimeInMillis();

    boolean hit = result.trigger().isHit(startedAt, millis - 1, millis + 1);

    System.out.println("fM4jH39ZCd :: hit = " + hit);

    assertThat(hit).isTrue();
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
  public void parse_month(int monthNumber, String monthName) {

    //
    //
    TriggerParseResult result = TriggerParser.parse("11:00 * " + monthName);
    //
    //

    List<TriggerParseError> errors = result.errors();

    Optional<String> error = ScheduledParseException.generateErrorMessage(errors);

    System.out.println("I1EKN9xwM2 :: " + error);

    System.out.println("GrNZ4v9iqc :: result = " + result.trigger());

    Calendar calendar = new GregorianCalendar();
    calendar.setTime(RND.dateYears(-3, 0));
    calendar.set(Calendar.MONTH, monthNumber);
    calendar.set(Calendar.HOUR_OF_DAY, 11);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    System.out.println("GhVySxwAHv :: calendar " + calendar.getTime()
                         + ", monthNumber = " + monthNumber
                         + ", monthName = " + monthName);

    long millis = calendar.getTimeInMillis();
    calendar.add(Calendar.HOUR, -3);
    long startedAt = calendar.getTimeInMillis();

    boolean hit = result.trigger().isHit(startedAt, millis - 1, millis + 1);

    System.out.println("b75iBypR38 :: hit = " + hit);

    assertThat(hit).isTrue();
  }
}
