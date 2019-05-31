package kz.greetgo.scheduling;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
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
  public void match(String pattern, String nowStr, boolean expectedResult) throws ParseException {

    String prevMatchStr = "2014-01-01 11:10:00";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    TaskRunStatus taskRunStatus = new TaskRunStatus();

    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher(pattern, "in test", taskRunStatus);
    //
    //
    assertThat(matcher.isParallel()).isFalse();

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
  public void match_complex() {
    TaskRunStatus taskRunStatus = new TaskRunStatus();
    new SchedulerMatcher("12/3:0/3 (03-07,11,14-18,21) {понедельник,вт,ср} [март-июнь]",
      "in match_complex", taskRunStatus);
  }

  @Test
  public void match_off() throws ParseException {
    String prevMatchStr = "2014-01-01 11:10:00";
    String nowStr = "2015-01-01 11:30:00";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    TaskRunStatus taskRunStatus = new TaskRunStatus();

    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher("off 11:30", "in test", taskRunStatus);
    //
    //

    assertThat(matcher.isParallel()).isFalse();

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
      new Object[]{"паралельно"},
    };
  }

  @Test(dataProvider = "match_parallel_variants")
  public void match_parallel_1(String parallelVariant) {
    TaskRunStatus taskRunStatus = new TaskRunStatus();

    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher(parallelVariant + " off 11:30", "in test", taskRunStatus);
    //
    //
    assertThat(matcher.isParallel()).isTrue();
  }


  @Test(dataProvider = "match_parallel_variants")
  public void match_parallel_3(String parallelVariant) {
    TaskRunStatus taskRunStatus = new TaskRunStatus();

    //
    //
    final SchedulerMatcher matcher = new SchedulerMatcher(parallelVariant + " 11:30", "in test", taskRunStatus);
    //
    //
    assertThat(matcher.isParallel()).isTrue();
  }

  @SuppressWarnings("SameParameterValue")
  private static long at(String strTime) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.parse(strTime).getTime();
  }

  @DataProvider
  public Object[][] match_repeat_13_17_minutes_DataSource() {
    return new Object[][]{

      new Object[]{1, true, " повторять каждые 13 мин, начиная с паузы 17 мин"},
      new Object[]{2, false, "повторять каждые 13 мин, начиная с паузы 17 мин"},
      new Object[]{3, true, " repeat every 13 min after pause in 17 minutes"},
      new Object[]{4, false, "repeat every 13 minutes after pause in 17 min"},

      new Object[]{5, true, " repeat every 780 sec after pause in 17 minutes"},
      new Object[]{6, false, "repeat every 780 seconds after pause in 17 min"},

    };
  }

  @Test(dataProvider = "match_repeat_13_17_minutes_DataSource")
  public void match_repeat_13_17_minutes(int id, boolean parallel, String pattern) {
    String p = parallel ? "параллельно " : "";

    TaskRunStatus taskRunStatus = new TaskRunStatus();

    long start = System.currentTimeMillis();
    taskRunStatus.schedulerStartedAt.set(start);

    final SchedulerMatcher matcher = new SchedulerMatcher(p + pattern, "in test", taskRunStatus);

    assertThat(matcher.isParallel()).isEqualTo(parallel);

    long _17_min = 1000 * 60 * 17;
    long _13_min = 1000 * 60 * 13;

    assertThat(matcher.match(0, start + _13_min)).describedAs("id = " + id).isFalse();

    assertThat(matcher.match(0, start + _17_min + 1)).describedAs("id = " + id).isTrue();
    assertThat(matcher.match(0, start + _17_min + 1)).describedAs("id = " + id).isFalse();
    assertThat(matcher.match(0, start + _17_min + 10)).describedAs("id = " + id).isFalse();

    assertThat(matcher.match(0, start + _17_min + _13_min + 2)).describedAs("id = " + id).isTrue();
    assertThat(matcher.match(0, start + _17_min + _13_min + 2)).describedAs("id = " + id).isFalse();
    assertThat(matcher.match(0, start + _17_min + _13_min + 20)).describedAs("id = " + id).isFalse();

    long startedAt = start + _17_min + _13_min + 70;

    taskRunStatus.lastStartedAt.set(startedAt);

    if (parallel) {
      assertThat(matcher.match(0, startedAt + _13_min * 2)).describedAs("id = " + id).isTrue();
      assertThat(matcher.match(0, startedAt + _13_min * 2)).describedAs("id = " + id).isFalse();
    } else {
      assertThat(matcher.match(0, startedAt + _13_min * 2)).describedAs("id = " + id).isTrue();
    }

    taskRunStatus.lastFinishedAt.set(startedAt + _13_min * 3 + 1);

    if (parallel) {
      assertThat(matcher.match(0, startedAt + _13_min * 3 + 5)).describedAs("id = " + id).isTrue();
      assertThat(matcher.match(0, startedAt + _13_min * 3 + 5)).describedAs("id = " + id).isFalse();
    } else {
      assertThat(matcher.match(0, startedAt + _13_min * 3 + 5)).describedAs("id = " + id).isFalse();

      assertThat(matcher.match(0, startedAt + _13_min * 4 + 5)).describedAs("id = " + id).isTrue();
      assertThat(matcher.match(0, startedAt + _13_min * 4 + 5)).describedAs("id = " + id).isFalse();
    }
  }

  @DataProvider
  public Object[][] match_repeat_13_minutes_DataSource() {
    return new Object[][]{

      new Object[]{1, true, " повторять каждые 13 мин"},
      new Object[]{2, false, "повторять каждые 13 минут"},
      new Object[]{3, true, " repeat every 13 min"},
      new Object[]{4, false, "repeat every 13 minutes"},

      new Object[]{5, true, " повторять каждые 780 сек"},
      new Object[]{6, false, "повторять каждые 780 секунд"},
      new Object[]{7, true, " repeat every 780 sec"},
      new Object[]{8, false, "repeat every 780 seconds"},

    };
  }

  @Test(dataProvider = "match_repeat_13_minutes_DataSource")
  public void match_repeat_13_minutes(int id, boolean parallel, String pattern) throws ParseException {
    String p = parallel ? "параллельно " : "";

    long start = at("2014-01-01 11:27:00");

    TaskRunStatus taskRunStatus = new TaskRunStatus();
    taskRunStatus.schedulerStartedAt.set(start);

    final SchedulerMatcher matcher = new SchedulerMatcher(p + pattern, "in test", taskRunStatus);

    assertThat(matcher.isParallel()).isEqualTo(parallel);

    long _13_min = 1000 * 60 * 13;

    assertThat(matcher.match(0, start + 1)).describedAs("id = " + id).isTrue();
    assertThat(matcher.match(0, start + 1)).describedAs("id = " + id).isFalse();

    assertThat(matcher.match(0, start + _13_min + 2)).describedAs("id = " + id).isTrue();
    assertThat(matcher.match(0, start + _13_min + 2)).describedAs("id = " + id).isFalse();
    assertThat(matcher.match(0, start + _13_min + 20)).describedAs("id = " + id).isFalse();

    assertThat(matcher.match(0, start + _13_min * 2 + 3)).describedAs("id = " + id).isTrue();
    assertThat(matcher.match(0, start + _13_min * 2 + 3)).describedAs("id = " + id).isFalse();
    assertThat(matcher.match(0, start + _13_min * 2 + 30)).describedAs("id = " + id).isFalse();

    long startedAt = start + _13_min * 3 + 70;

    taskRunStatus.lastStartedAt.set(startedAt);

    if (parallel) {
      assertThat(matcher.match(0, startedAt + _13_min * 2)).describedAs("id = " + id).isTrue();
      assertThat(matcher.match(0, startedAt + _13_min * 2)).describedAs("id = " + id).isFalse();
    } else {
      assertThat(matcher.match(0, startedAt + _13_min * 2)).describedAs("id = " + id).isTrue();
    }

    taskRunStatus.lastFinishedAt.set(startedAt + _13_min * 3 + 1);

    if (parallel) {
      assertThat(matcher.match(0, startedAt + _13_min * 3 + 7)).describedAs("id = " + id).isTrue();
      assertThat(matcher.match(0, startedAt + _13_min * 3 + 7)).describedAs("id = " + id).isFalse();
    } else {
      assertThat(matcher.match(0, startedAt + _13_min * 3 + 7)).describedAs("id = " + id).isFalse();

      assertThat(matcher.match(0, startedAt + _13_min * 4 + 7)).describedAs("id = " + id).isTrue();
      assertThat(matcher.match(0, startedAt + _13_min * 4 + 7)).describedAs("id = " + id).isFalse();
    }
  }
}
