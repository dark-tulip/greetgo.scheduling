package kz.greetgo.scheduling;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;

import static org.fest.assertions.api.Assertions.assertThat;

public class SchedulerMatcherTest {

  @DataProvider
  public Object[][] match_dataProvider() {
    return new Object[][]{

      new Object[]{":10", "2014-01-01 11:10:00", false},
      new Object[]{":10", "2015-01-02 11:10:00", true},
      new Object[]{":10", "2015-01-02 12:10:00", true},
      new Object[]{":10", "2015-01-02 13:11:00", false},

      new Object[]{"11:10", "2015-01-02 11:10:00", true},
      new Object[]{"11:10", "2015-01-02 12:10:00", false},

      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-02 13:30:00", false},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-11 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-12 13:30:00", false},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-13 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-14 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-15 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-20 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-21 13:30:00", false},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-22 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-23 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-25 13:30:00", true},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-26 13:30:00", false},
      new Object[]{"13:30 (11,13-20,22-25)", "2015-03-27 13:30:00", false},

      new Object[]{"13:30 (11,13,17)", "2015-03-10 13:30:00", false},
      new Object[]{"13:30 (11,13,17)", "2015-03-11 13:30:00", true},
      new Object[]{"13:30 (11,13,17)", "2015-03-12 13:30:00", false},
      new Object[]{"13:30 (11,13,17)", "2015-03-13 13:30:00", true},
      new Object[]{"13:30 (11,13,17)", "2015-03-14 13:30:00", false},

      //2015-03-16 - Понедельник
      //2015-03-17 - Вторник
      //2015-03-18 - Среда
      //2015-03-19 - Четверг
      //2015-03-20 - Пятница
      //2015-03-21 - Суббота
      //2015-03-22 - Воскресенье

      new Object[]{"13:30 {Пн,Ср}", "2015-03-16 13:30:00", true},
      new Object[]{"13:30 {Пн,Ср}", "2015-03-17 13:30:00", false},
      new Object[]{"13:30 {Пн,Ср}", "2015-03-18 13:30:00", true},
      new Object[]{"13:30 {Пн,Ср}", "2015-03-19 13:30:00", false},
      new Object[]{"13:30 {Пн,Ср}", "2015-03-20 13:30:00", false},
      new Object[]{"13:30 {Пн,Ср}", "2015-03-21 13:30:00", false},
      new Object[]{"13:30 {Пн,Ср}", "2015-03-22 13:30:00", false},

      new Object[]{"13:30 {Среды-Субботу}", "2015-03-16 13:30:00", false},
      new Object[]{"13:30 {Среды-Субботу}", "2015-03-17 13:30:00", false},
      new Object[]{"13:30 {Среды-Субботу}", "2015-03-18 13:30:00", true},
      new Object[]{"13:30 {Среды-Субботу}", "2015-03-19 13:30:00", true},
      new Object[]{"13:30 {Среды-Субботу}", "2015-03-20 13:30:00", true},
      new Object[]{"13:30 {Среды-Субботу}", "2015-03-21 13:30:00", true},
      new Object[]{"13:30 {Среды-Субботу}", "2015-03-22 13:30:00", false},

      //2015-03-16 - Понедельник
      //2015-03-17 - Вторник
      //2015-03-18 - Среда
      //2015-03-19 - Четверг
      //2015-03-20 - Пятница
      //2015-03-21 - Суббота
      //2015-03-22 - Воскресенье

      //ПОНЕДЕЛЬНИК
      new Object[]{"13:30 {Понедельник}", "2015-03-16 13:30:00", true},
      new Object[]{"13:30 {Пон}        ", "2015-03-16 13:30:00", true},
      new Object[]{"13:30 {Пн}         ", "2015-03-16 13:30:00", true},
      new Object[]{"13:30 {Понед}      ", "2015-03-16 13:30:00", true},
      new Object[]{"13:30 {Mo}         ", "2015-03-16 13:30:00", true},
      new Object[]{"13:30 {Mon}        ", "2015-03-16 13:30:00", true},

      //ВТОРНИК
      new Object[]{"13:30 {Вторник}", "2015-03-17 13:30:00", true},
      new Object[]{"13:30 {Вт}     ", "2015-03-17 13:30:00", true},
      new Object[]{"13:30 {Вто}    ", "2015-03-17 13:30:00", true},
      new Object[]{"13:30 {Втор}   ", "2015-03-17 13:30:00", true},
      new Object[]{"13:30 {Tu}     ", "2015-03-17 13:30:00", true},
      new Object[]{"13:30 {Tue}    ", "2015-03-17 13:30:00", true},

      //СРЕДА
      new Object[]{"13:30 {Среда}", "2015-03-18 13:30:00", true},
      new Object[]{"13:30 {Ср}   ", "2015-03-18 13:30:00", true},
      new Object[]{"13:30 {We}   ", "2015-03-18 13:30:00", true},

      //ЧЕТВЕРГ
      new Object[]{"13:30 {Четверг}", "2015-03-19 13:30:00", true},
      new Object[]{"13:30 {Чт}     ", "2015-03-19 13:30:00", true},
      new Object[]{"13:30 {Th}     ", "2015-03-19 13:30:00", true},

      //ПЯТНИЦА
      new Object[]{"13:30 {Пятница}", "2015-03-20 13:30:00", true},
      new Object[]{"13:30 {Пт}     ", "2015-03-20 13:30:00", true},
      new Object[]{"13:30 {Fr}     ", "2015-03-20 13:30:00", true},

      //СУББОТА
      new Object[]{"13:30 {Суббота}", "2015-03-21 13:30:00", true},
      new Object[]{"13:30 {Сб}     ", "2015-03-21 13:30:00", true},
      new Object[]{"13:30 {Sa}     ", "2015-03-21 13:30:00", true},

      //ВОСКРЕСЕНЬЕ
      new Object[]{"13:30 {Воскресенье}", "2015-03-22 13:30:00", true},
      new Object[]{"13:30 {Вс}         ", "2015-03-22 13:30:00", true},
      new Object[]{"13:30 {Su}         ", "2015-03-22 13:30:00", true},

      // --- М Е С Я Ц Ы ---

      new Object[]{"13:30 [Январь]   ", "2015-01-21 13:30:00", true},
      new Object[]{"13:30 [Февраль]  ", "2015-02-21 13:30:00", true},
      new Object[]{"13:30 [Март]     ", "2015-03-21 13:30:00", true},
      new Object[]{"13:30 [Апрель]   ", "2015-04-21 13:30:00", true},
      new Object[]{"13:30 [Май]      ", "2015-05-21 13:30:00", true},
      new Object[]{"13:30 [Июнь]     ", "2015-06-21 13:30:00", true},
      new Object[]{"13:30 [Июль]     ", "2015-07-21 13:30:00", true},
      new Object[]{"13:30 [Август]   ", "2015-08-21 13:30:00", true},
      new Object[]{"13:30 [Сентябрь] ", "2015-09-21 13:30:00", true},
      new Object[]{"13:30 [Октябрь]  ", "2015-10-21 13:30:00", true},
      new Object[]{"13:30 [Ноябрь]   ", "2015-11-21 13:30:00", true},
      new Object[]{"13:30 [Декабрь]  ", "2015-12-21 13:30:00", true},

      new Object[]{"13:30 [January]   ", "2015-01-21 13:30:00", true},
      new Object[]{"13:30 [February]  ", "2015-02-21 13:30:00", true},
      new Object[]{"13:30 [March]     ", "2015-03-21 13:30:00", true},
      new Object[]{"13:30 [April]     ", "2015-04-21 13:30:00", true},
      new Object[]{"13:30 [May]       ", "2015-05-21 13:30:00", true},
      new Object[]{"13:30 [June]      ", "2015-06-21 13:30:00", true},
      new Object[]{"13:30 [July]      ", "2015-07-21 13:30:00", true},
      new Object[]{"13:30 [August]    ", "2015-08-21 13:30:00", true},
      new Object[]{"13:30 [September] ", "2015-09-21 13:30:00", true},
      new Object[]{"13:30 [October]   ", "2015-10-21 13:30:00", true},
      new Object[]{"13:30 [November]  ", "2015-11-21 13:30:00", true},
      new Object[]{"13:30 [December]  ", "2015-12-21 13:30:00", true},

      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-01-21 13:30:00", false},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-02-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-03-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-04-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-05-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-06-21 13:30:00", false},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-07-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-08-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-09-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-10-21 13:30:00", false},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-11-21 13:30:00", true},
      new Object[]{"13:30 [feb,march-may,июль-sep,Ноябрь]", "2015-12-21 13:30:00", false},

      // слэш синтаксис

      new Object[]{":13/3", "2015-12-21 13:00:00", false},
      new Object[]{":13/3", "2015-12-21 13:01:00", false},
      new Object[]{":13/3", "2015-12-21 13:02:00", false},
      new Object[]{":13/3", "2015-12-21 13:03:00", false},
      new Object[]{":13/3", "2015-12-21 13:04:00", false},
      new Object[]{":13/3", "2015-12-21 13:05:00", false},
      new Object[]{":13/3", "2015-12-21 13:13:00", true},
      new Object[]{":13/3", "2015-12-21 13:14:00", false},
      new Object[]{":13/3", "2015-12-21 13:15:00", false},
      new Object[]{":13/3", "2015-12-21 13:16:00", true},
      new Object[]{":13/3", "2015-12-21 13:17:00", false},
      new Object[]{":13/3", "2015-12-21 13:18:00", false},
      new Object[]{":13/3", "2015-12-21 13:19:00", true},
      new Object[]{":13/3", "2015-12-21 13:20:00", false},
      new Object[]{":13/3", "2015-12-21 13:21:00", false},
      new Object[]{":13/3", "2015-12-21 13:22:00", true},
      new Object[]{":13/3", "2015-12-21 13:23:00", false},
      new Object[]{":13/3", "2015-12-21 13:24:00", false},
      new Object[]{":13/3", "2015-12-21 13:25:00", true},

      new Object[]{":0/7", "2015-12-21 13:00:00", true},
      new Object[]{":0/7", "2015-12-21 13:01:00", false},
      new Object[]{":0/7", "2015-12-21 13:07:00", true},
      new Object[]{":0/7", "2015-12-21 13:14:00", true},
      new Object[]{":0/7", "2015-12-21 13:13:00", false},

      new Object[]{"13/3:0/7", "2015-12-21 13:07:00", true},
      new Object[]{"13/3:0/7", "2015-12-21 13:08:00", false},
      new Object[]{"13/3:0/7", "2015-12-21 14:07:00", false},
      new Object[]{"13/3:0/7", "2015-12-21 16:14:00", true},
      new Object[]{"13/3:0/7", "2015-12-21 19:21:00", true},
      new Object[]{"13/3:0/7", "2015-12-21 20:20:00", false},
      new Object[]{"13/3:0/7", "2015-12-21 21:21:00", false},
    };
  }

  @Test(dataProvider = "match_dataProvider")
  public void match(String pattern, String nowStr, boolean expectedResult) throws Exception {

    String prevMatchStr = "2014-01-01 11:10:00";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher(pattern, "in test");
    //
    //
    assertThat(matcher.parallel).isFalse();


    final long prevMatch = sdf.parse(prevMatchStr).getTime();
    final long now = sdf.parse(nowStr).getTime();

    //
    //
    final boolean actualResult = matcher.match(prevMatch, now);
    //
    //

    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  public void match_complex() throws Exception {
    new SchedulerMatcher("12/3:0/3 (03-07,11,14-18,21) {понедельник,вт,ср} [март-июнь]", "in match_complex");
  }

  @DataProvider
  public Object[][] toIntDayOfWeek_data() {
    return new Object[][]{

      new Object[]{"Воскресенье", 1},
      new Object[]{"Понедельник", 2},
      new Object[]{"Вторник", 3},
      new Object[]{"Среда", 4},
      new Object[]{"Четверг", 5},
      new Object[]{"Пятница", 6},
      new Object[]{"Суббота", 7},

      new Object[]{"Вс", 1},
      new Object[]{"Пн", 2},
      new Object[]{"Вт", 3},
      new Object[]{"Ср", 4},
      new Object[]{"Чт", 5},
      new Object[]{"Пт", 6},
      new Object[]{"Сб", 7},

      new Object[]{"Вос", 1},
      new Object[]{"Пон", 2},
      new Object[]{"Вто", 3},
      new Object[]{"Сре", 4},
      new Object[]{"Чет", 5},
      new Object[]{"Пят", 6},
      new Object[]{"Суб", 7},

      new Object[]{"Sunday", 1},
      new Object[]{"Monday", 2},
      new Object[]{"Tuesday", 3},
      new Object[]{"Wednesday", 4},
      new Object[]{"Thursday", 5},
      new Object[]{"Friday", 6},
      new Object[]{"Saturday", 7},

      new Object[]{"Sun", 1},
      new Object[]{"Mon", 2},
      new Object[]{"Tue", 3},
      new Object[]{"Wed", 4},
      new Object[]{"Thu", 5},
      new Object[]{"Fri", 6},
      new Object[]{"Sat", 7},

    };
  }

  @Test(dataProvider = "toIntDayOfWeek_data")
  public void dayOfWeekToInt(String dayName, int expectedValue) throws Exception {

    //
    //
    final int actualValue = SchedulerMatcher.dayOfWeekToInt(dayName, "test pattern", "test place");
    //
    //

    assertThat(actualValue).isEqualTo(expectedValue);
  }

  @DataProvider
  public Object[][] monthToInt_data() {
    return new Object[][]{
      new Object[]{"Январь", 1},
      new Object[]{"Февраль", 2},
      new Object[]{"Март", 3},
      new Object[]{"Апрель", 4},
      new Object[]{"Май", 5},
      new Object[]{"Июнь", 6},
      new Object[]{"Июль", 7},
      new Object[]{"Август", 8},
      new Object[]{"Сентабрь", 9},
      new Object[]{"Октябрь", 10},
      new Object[]{"Ноябрь", 11},
      new Object[]{"Декабрь", 12},
      new Object[]{"January", 1},
      new Object[]{"February", 2},
      new Object[]{"March", 3},
      new Object[]{"April", 4},
      new Object[]{"May", 5},
      new Object[]{"June", 6},
      new Object[]{"July", 7},
      new Object[]{"AugUst", 8},
      new Object[]{"September", 9},
      new Object[]{"October", 10},
      new Object[]{"November", 11},
      new Object[]{"December", 12},

      new Object[]{"Янв", 1},
      new Object[]{"Фев", 2},
      new Object[]{"Мар", 3},
      new Object[]{"Апр", 4},
      new Object[]{"Май", 5},
      new Object[]{"Июн", 6},
      new Object[]{"Июл", 7},
      new Object[]{"АвГ", 8},
      new Object[]{"Сен", 9},
      new Object[]{"Окт", 10},
      new Object[]{"Ноя", 11},
      new Object[]{"Дек", 12},
      new Object[]{"Jan", 1},
      new Object[]{"Feb", 2},
      new Object[]{"MaR", 3},
      new Object[]{"Apr", 4},
      new Object[]{"May", 5},
      new Object[]{"Jun", 6},
      new Object[]{"Jul", 7},
      new Object[]{"Aug", 8},
      new Object[]{"Sep", 9},
      new Object[]{"Oct", 10},
      new Object[]{"Nov", 11},
      new Object[]{"Dec", 12},

    };
  }

  @Test(dataProvider = "monthToInt_data")
  public void monthToInt(String dayName, int expectedValue) throws Exception {

    //
    //
    final int actual = SchedulerMatcher.monthToInt(dayName, "test pattern", "test place");
    //
    //

    assertThat(actual).isEqualTo(expectedValue);
  }

  @Test
  public void match_off() throws Exception {
    String prevMatchStr = "2014-01-01 11:10:00";
    String nowStr = "2015-01-01 11:30:00";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher("off 11:30", "in test");
    //
    //

    assertThat(matcher.parallel).isFalse();

    final long prevMatch = sdf.parse(prevMatchStr).getTime();
    final long now = sdf.parse(nowStr).getTime();

    //
    //
    final boolean actualResult = matcher.match(prevMatch, now);
    //
    //

    assertThat(actualResult).isFalse();
  }

  @DataProvider
  public Object[][] match_parallel_variants() {
    return new Object[][]{
      new Object[]{"Parallel"},
      new Object[]{"Para"},
      new Object[]{"PARALLEL"},
      new Object[]{"PARA"},
      new Object[]{"parallel"},
      new Object[]{"para"},

      new Object[]{"Паралель"},
      new Object[]{"Пара"},
      new Object[]{"паралель"},
      new Object[]{"пара"},
      new Object[]{"ПАРАЛЕЛЬ"},
      new Object[]{"ПАРА"},
    };
  }

  @Test(dataProvider = "match_parallel_variants")
  public void match_parallel_1(String parallelVariant) throws Exception {
    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher(parallelVariant + " off 11:30", "in test");
    //
    //
    assertThat(matcher.parallel).isTrue();
  }

  @Test(dataProvider = "match_parallel_variants")
  public void match_parallel_2(String parallelVariant) throws Exception {
    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher("off " + parallelVariant + " 11:30", "in test");
    //
    //
    assertThat(matcher.parallel).isTrue();
  }

  @Test(dataProvider = "match_parallel_variants")
  public void match_parallel_3(String parallelVariant) throws Exception {
    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher(parallelVariant + " 11:30", "in test");
    //
    //
    assertThat(matcher.parallel).isTrue();
  }
}
