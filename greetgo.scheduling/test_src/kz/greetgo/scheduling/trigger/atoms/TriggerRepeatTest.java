package kz.greetgo.scheduling.trigger.atoms;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerRepeatTest {

  @SuppressWarnings("PointlessArithmeticExpression")
  @DataProvider
  public Object[][] isHitDataProvider() {
    return new Object[][]{
      //@formatter:off
      {  0, 1000, 1_000_000, 1_000_000 +    0 - 1, 1_000_000 +    0 + 1, true},
      {  0, 1000, 1_000_000, 1_000_000 + 1000 - 1, 1_000_000 + 1000 + 1, true},
      {  0, 1000, 1_000_000, 1_000_000 + 2000 - 1, 1_000_000 + 2000 + 1, true},
      {  0, 1000, 1_000_000, 1_000_000 + 3000 - 1, 1_000_000 + 3000 + 1, true},
      {  0, 1000, 1_000_000, 1_000_000 + 4000 - 1, 1_000_000 + 4000 + 1, true},

      {700, 1000, 1_000_000, 1_000_000 +  700 - 1, 1_000_000 +  700 + 1, true},
      {700, 1000, 1_000_000, 1_000_000 + 1700 - 1, 1_000_000 + 1700 + 1, true},
      {700, 1000, 1_000_000, 1_000_000 + 2700 - 1, 1_000_000 + 2700 + 1, true},
      {700, 1000, 1_000_000, 1_000_000 + 3700 - 1, 1_000_000 + 3700 + 1, true},
      {700, 1000, 1_000_000, 1_000_000 + 4700 - 1, 1_000_000 + 4700 + 1, true},

      {700, 1000, 1_000_000, 1_000_000 +  700 + 1, 1_000_000 +  700 + 690, false},
      {700, 1000, 1_000_000, 1_000_000 + 1700 + 1, 1_000_000 + 1700 + 690, false},
      {700, 1000, 1_000_000, 1_000_000 + 2700 + 1, 1_000_000 + 2700 + 690, false},
      {700, 1000, 1_000_000, 1_000_000 + 3700 + 1, 1_000_000 + 3700 + 690, false},
      {700, 1000, 1_000_000, 1_000_000 + 4700 + 1, 1_000_000 + 4700 + 690, false},


      //@formatter:on
    };
  }

  @Test(dataProvider = "isHitDataProvider")
  public void isHit(long silentPeriod, long delay, long startedAt, long from, long to, boolean expectedResult) {

    TriggerRepeat trigger = new TriggerRepeat(silentPeriod, delay);

    //
    //
    boolean hit = trigger.isHit(startedAt, from, to);
    //
    //

    assertThat(hit).isEqualTo(expectedResult);
    assertThat(trigger.isDotty()).isTrue();
  }
}
