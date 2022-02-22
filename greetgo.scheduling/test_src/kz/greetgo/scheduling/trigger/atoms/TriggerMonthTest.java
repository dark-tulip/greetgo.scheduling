package kz.greetgo.scheduling.trigger.atoms;

import kz.greetgo.scheduling.trigger.inner_logic.Range;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerMonthTest {
  @DataProvider
  public Object[][] isHitDataProvider() {
    return new Object[][]{

      {1, "2018-04-11 22:22:22", "2018-04-11 11:23:19", false},

      {4, "2018-04-11 11:23:18", "2018-04-11 11:23:19", true},

      {3, "2018-01-14 11:23:18", "2018-06-16 11:23:19", true},

      {7, "2018-01-14 11:23:18", "2020-06-16 11:23:19", true},

      {10, "2018-01-08 11:23:18", "2018-01-11 11:23:19", false},

      {4, "2018-04-11 11:23:20", "2018-04-11 11:23:19", false},

    };
  }

  @Test(dataProvider = "isHitDataProvider")
  public void isHit(int month, String fromStr, String toStr, boolean expectedResult) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Date from = sdf.parse(fromStr);
    Date to   = sdf.parse(toStr);

    TriggerMonth trigger = new TriggerMonth(Range.of(month, month));

    //
    //
    boolean result = trigger.isHit(0, from.getTime(), to.getTime());
    //
    //

    assertThat(result).isEqualTo(expectedResult);
    assertThat(trigger.isDotty()).isFalse();

  }

}
