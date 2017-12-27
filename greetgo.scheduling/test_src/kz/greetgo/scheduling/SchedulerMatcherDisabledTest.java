package kz.greetgo.scheduling;

import org.testng.annotations.Test;

import static kz.greetgo.scheduling.SchedulerMatcherDisabled.isDisabled;
import static org.fest.assertions.api.Assertions.assertThat;

public class SchedulerMatcherDisabledTest {

  @Test
  public void isDisabled_1() throws Exception {

    assertThat(isDisabled(" off left ")).isTrue();
    assertThat(isDisabled(" off ")).isTrue();
    assertThat(isDisabled("off")).isTrue();
    assertThat(isDisabled("left")).isFalse();
    assertThat(isDisabled(" left ")).isFalse();
    assertThat(isDisabled(" left asd ")).isFalse();

    assertThat(isDisabled(" отключено left ")).isTrue();
    assertThat(isDisabled(" выключено ")).isTrue();
    assertThat(isDisabled("откл")).isTrue();
    assertThat(isDisabled("работает")).isFalse();
    assertThat(isDisabled(" работает ")).isFalse();
    assertThat(isDisabled(" хорошо работает ")).isFalse();


  }
}