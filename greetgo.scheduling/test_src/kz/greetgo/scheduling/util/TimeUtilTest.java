package kz.greetgo.scheduling.util;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TimeUtilTest {

  @Test
  public void millisToHms_hmsToMillis__1() {

    long millis = TimeUtil.hmsToMillis("23:44:23");
    String hms = TimeUtil.millisToHms(millis);

    assertThat(hms).isEqualTo("23:44:23");

  }

  @Test
  public void millisToHms_hmsToMillis__2() {

    long millis = TimeUtil.hmsToMillis("11:12");
    String hms = TimeUtil.millisToHms(millis);

    assertThat(hms).isEqualTo("11:12:00");

  }


}
