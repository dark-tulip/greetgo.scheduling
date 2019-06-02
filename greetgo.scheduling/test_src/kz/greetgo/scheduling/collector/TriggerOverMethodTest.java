package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.Scheduled;
import kz.greetgo.scheduling.exceptions.ScheduledParseException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static org.fest.assertions.api.Assertions.assertThat;

public class TriggerOverMethodTest {

  Supplier<Long> currentTimeMillis = null;

  final SchedulerConfigStoreInMemory configStore = new SchedulerConfigStoreInMemory(() -> currentTimeMillis);

  private final TriggerConfigSource triggerConfigSource = new TriggerConfigSource() {
    @Override
    public SchedulerConfigStore schedulerConfigStore() {
      return configStore;
    }

    @Override
    public String configExtension() {
      return ".config.txt";
    }

    @Override
    public String configErrorsExtension() {
      return ".errors.txt";
    }
  };

  @BeforeMethod
  public void clearAll() {
    configStore.clear();
    currentTimeMillis = null;
  }

  @Test(expectedExceptions = ScheduledParseException.class)
  public void withoutFromConfig_errorInSchedulerPattern() {

    Scheduled scheduled = new ScheduledFix("asd");

    TriggerConfig triggerConfig = new TriggerConfigTest(triggerConfigSource, "test");

    TriggerOverMethod.create("hello", triggerConfig, scheduled, null, null, 0, currentTimeMillis);

  }

  @Test
  public void withoutFromConfig_ok() {

    Scheduled scheduled = new ScheduledFix("11:17");

    TriggerConfig triggerConfig = new TriggerConfigTest(triggerConfigSource, "test");

    TriggerOverMethod trigger = TriggerOverMethod.create("hello", triggerConfig, scheduled, null, null, 0, currentTimeMillis);

    System.out.println("5h4b2v6 :: trigger = " + trigger);

    assertThat(trigger.toString()).isEqualTo("Wrapper[DayPoint{11:17:00}]");

  }

  @Test
  public void withFromConfig_justStarted() {

    currentTimeMillis = SupplierSeq.of(1L, 2L, 3L, 4L);

    Scheduled scheduled = new ScheduledFix("11:17");

    FromConfigFix fromConfig = new FromConfigFix("Описание 1\nОписание 2");

    TriggerConfig triggerConfig = new TriggerConfigTest(triggerConfigSource, "test");

    TriggerOverMethod trigger = TriggerOverMethod.create(
      "hello", triggerConfig, scheduled, fromConfig,
      "Линия1\nЛиния2", 10, currentTimeMillis
    );

    trigger.isParallel();


  }

}
