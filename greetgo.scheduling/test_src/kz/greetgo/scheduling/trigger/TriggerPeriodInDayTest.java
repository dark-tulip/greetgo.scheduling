package kz.greetgo.scheduling.trigger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class TriggerPeriodInDayTest {

  @DataProvider
  Object[][] isHitDataProvider() {
    return new Object[][]{
      {"20:11:28", "21:23:17", "2019-01-23 21:23:16", "2019-01-23 21:23:18", true},
      {"20:11:28", "21:23:17", "2019-01-23 21:23:19", "2019-01-23 21:23:20", false},
      {"20:11:28", "21:23:17", "2019-01-23 20:11:27", "2019-01-23 20:11:29", true},
      {"20:11:28", "21:23:17", "2019-01-23 20:11:00", "2019-01-23 20:11:01", false},
    };
  }

  @Test(dataProvider = "isHitDataProvider")
  public void isHit(String fromStr, String toStr,
                    String checkFromStr, String checkToStr,
                    boolean expectedHit) throws ParseException {

    TriggerPeriodInDay trigger = new TriggerPeriodInDay(fromStr, toStr);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Date checkFrom = sdf.parse(checkFromStr);
    Date checkTo = sdf.parse(checkToStr);

    //
    //
    boolean hit = trigger.isHit(0, checkFrom.getTime(), checkTo.getTime());
    //
    //

    assertThat(hit).isEqualTo(expectedHit);
    assertThat(trigger.isDotty()).isFalse();

  }

}
