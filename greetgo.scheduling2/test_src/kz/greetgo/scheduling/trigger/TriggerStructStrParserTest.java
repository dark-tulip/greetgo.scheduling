package kz.greetgo.scheduling.trigger;

import org.testng.annotations.Test;

public class TriggerStructStrParserTest {

  @Test
  public void name() {

    String source = "повторять каждые 10 секунд";

    TriggerStructStrParser parser = TriggerStructStrParser.of(Range.of(0, 0), source, () -> 1000);

    Trigger trigger = parser.parse();

  }
}
