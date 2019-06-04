package kz.greetgo.scheduling.collector;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerOverMethodTest {

  @Test
  public void withoutFromFile() {

    ScheduledDefinition definition = new ScheduledDefinition("name1", "12:00", false, null);

    TriggerOverMethod triggerOverMethod = TriggerOverMethod.create(definition, null);

    assertThat(triggerOverMethod.toString()).contains("12:00:00");

  }

}
