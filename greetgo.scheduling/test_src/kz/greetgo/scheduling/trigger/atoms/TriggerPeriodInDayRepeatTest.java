package kz.greetgo.scheduling.trigger.atoms;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kz.greetgo.scheduling.util.TimeUtil.MILLIS_MINUTE;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("PointlessArithmeticExpression")
public class TriggerPeriodInDayRepeatTest {

  @DataProvider
  Object[][] isHit_DP() {
    return new Object[][]{

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 12:12:00", "2019-01-23 17:12:00", false},

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:29:59", "2019-01-23 11:30:01", true},

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:30:59", "2019-01-23 11:31:01", true},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:30:58", "2019-01-23 11:30:59", false},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:31:01", "2019-01-23 11:31:02", false},

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:36:59", "2019-01-23 11:37:01", true},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:36:58", "2019-01-23 11:36:59", false},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:37:01", "2019-01-23 11:37:02", false},

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:28:59", "2019-01-23 11:29:01", false},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:00:00", "2019-01-23 11:29:59", false},

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 12:10:59", "2019-01-23 12:11:01", true},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 12:11:59", "2019-01-23 12:12:01", false},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 12:11:01", "2019-01-23 18:00:00", false},

      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:59:59", "2019-01-23 12:00:01", true},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 11:59:58", "2019-01-23 11:59:59", false},
      {"11:30", "12:11", 1 * MILLIS_MINUTE, "2019-01-23 12:00:01", "2019-01-23 12:00:02", false},

      {"08:00", "18:00", 30 * MILLIS_MINUTE, "2019-12-19 07:59:59", "2019-12-19 08:00:01", true},

    };
  }

  @Test(dataProvider = "isHit_DP")
  public void isHit(String hmsFrom, String hmsTo, long period,
                    String fromStr, String toStr,
                    boolean expected) throws ParseException {

    TriggerPeriodInDay periodInDay = new TriggerPeriodInDay(hmsFrom, hmsTo);

    TriggerPeriodInDayRepeat trigger = new TriggerPeriodInDayRepeat(periodInDay, period);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date from = sdf.parse(fromStr);
    Date to = sdf.parse(toStr);

    //
    //
    boolean hit = trigger.isHit(0, from.getTime(), to.getTime());
    //
    //

    assertThat(hit).isEqualTo(expected);

    assertThat(trigger.isDotty()).isTrue();

  }

  @Test
  public void specialVariant_08_00() throws ParseException {

    TriggerPeriodInDay periodInDay = new TriggerPeriodInDay("08:00", "18:00");
    TriggerPeriodInDayRepeat trigger = new TriggerPeriodInDayRepeat(periodInDay, 30 * MILLIS_MINUTE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long started = sdf.parse("2019-12-19 01:00:00").getTime();
    long it = sdf.parse("2019-12-19 08:00:00").getTime();

    boolean triggerHit = trigger.isHit(started, it - 1, it + 1);

    assertThat(triggerHit).isTrue();

  }

  @Test
  public void specialVariant_08_00__another() throws ParseException {

    TriggerPeriodInDay periodInDay = new TriggerPeriodInDay("08:00", "18:00");
    TriggerPeriodInDayRepeat trigger = new TriggerPeriodInDayRepeat(periodInDay, 30 * MILLIS_MINUTE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long started = sdf.parse("2019-12-19 01:00:00").getTime();
    long it = sdf.parse("2019-12-19 08:00:00").getTime();

    boolean triggerHit = trigger.isHit(started, it - 100, it + 100);

    assertThat(triggerHit).isTrue();

  }

  @Test
  public void specialVariant_08_30() throws ParseException {

    TriggerPeriodInDay periodInDay = new TriggerPeriodInDay("08:00", "18:00");
    TriggerPeriodInDayRepeat trigger = new TriggerPeriodInDayRepeat(periodInDay, 30 * MILLIS_MINUTE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long started = sdf.parse("2019-12-19 01:00:00").getTime();
    long it = sdf.parse("2019-12-19 08:30:00").getTime();

    boolean triggerHit = trigger.isHit(started, it - 1, it + 1);

    assertThat(triggerHit).isTrue();

  }
}
