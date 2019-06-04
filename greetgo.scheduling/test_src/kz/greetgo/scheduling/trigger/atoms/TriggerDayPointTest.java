package kz.greetgo.scheduling.trigger.atoms;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerDayPointTest {

  @DataProvider
  public Object[][] isHitDataProvider() {
    return new Object[][]{

      {"13:11:17", "2019-01-01 10:11:11", "2019-01-01 14:11:11", true},

      {"13:11:17", "2019-01-01 10:11:11", "2019-01-02 10:11:11", true},

      {"13:11:17", "2019-01-01 17:11:11", "2019-01-02 10:11:11", false},

      {"13:11:17", "2019-01-01 17:11:11", "2019-01-02 17:11:11", true},

      {"13:11:17", "2019-01-01 17:11:11", "2019-01-03 10:11:11", true},

      {"13:11:17", "2019-10-01 10:11:11", "2019-10-03 11:11:11", true},

      {"13:11:17", "2019-10-01 13:11:11", "2019-10-01 13:11:18", true},

      {"13:11:17", "2019-10-01 13:11:18", "2019-10-01 13:11:19", false},

    };
  }

  @Test(dataProvider = "isHitDataProvider")
  public void isHit(String dayPointStr, String fromStr, String toStr, boolean expectedHit) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Date from = sdf.parse(fromStr);
    Date to = sdf.parse(toStr);

    String[] dayPoint = dayPointStr.split(":");
    int hour = Integer.parseInt(dayPoint[0]);
    int minute = Integer.parseInt(dayPoint[1]);
    int second = Integer.parseInt(dayPoint[2]);

    TriggerDayPoint trigger = new TriggerDayPoint(hour, minute, second);

    //
    //
    boolean hit = trigger.isHit(0, from.getTime(), to.getTime());
    //
    //

    assertThat(hit).isEqualTo(expectedHit);
    assertThat(trigger.isDotty()).isTrue();

  }

  @Test
  public void concrete() {

    long from1 = 1559631060034L - 500L, to1 = 1559631060034L;
    long from2 = 1559631060034L, to2 = 1559631060534L;
    long from3 = 1559631060534L, to3 = 1559631061035L;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    System.out.println("hb325v :: from1 = " + sdf.format(new Date(from1)) + ", to1 = " + sdf.format(new Date(to1)));
    System.out.println("hb325v :: from2 = " + sdf.format(new Date(from2)) + ", to2 = " + sdf.format(new Date(to2)));

    TriggerDayPoint trigger = new TriggerDayPoint("12:51:00");

    System.out.println("2gv342v4 :: trigger = " + trigger);

    boolean hit1 = trigger.isHit(0, from1, to1);
    boolean hit2 = trigger.isHit(0, from2, to2);
    boolean hit3 = trigger.isHit(0, from3, to3);

    System.out.println("hit1 = " + hit1);
    System.out.println("hit2 = " + hit2);
    System.out.println("hit3 = " + hit3);

    assertThat(hit1).isTrue();
    assertThat(hit2).isFalse();
    assertThat(hit3).isFalse();

  }
}
