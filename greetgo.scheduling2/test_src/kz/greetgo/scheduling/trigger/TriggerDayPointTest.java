package kz.greetgo.scheduling.trigger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

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

}
