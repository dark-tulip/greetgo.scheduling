package kz.greetgo.scheduling.collector;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ControllerContextTest {

  @Test
  public void trigger() {

    long[] currentTime = new long[]{100};

    MemoryFileContent configFile = new MemoryFileContent(() -> currentTime[0]);
    MemoryFileContent errorFile = new MemoryFileContent(() -> currentTime[0]);

    String headerHelp = "Help line №1\nHelp №2\nLine №3";

    long checkFileDelayMillis = 500;

    ControllerContext controllerContext = new ControllerContext(
      configFile, errorFile, headerHelp, checkFileDelayMillis, () -> currentTime[0]
    );

    ScheduledDefinition definition = new ScheduledDefinition("name1", "11:21", true, "Config line №1\nConfig line №2");

    assertThat(1).isEqualTo(2);
  }
}
