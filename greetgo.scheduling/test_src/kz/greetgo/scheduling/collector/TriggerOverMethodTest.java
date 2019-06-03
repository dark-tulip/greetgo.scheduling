package kz.greetgo.scheduling.collector;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static org.fest.assertions.api.Assertions.assertThat;

public class TriggerOverMethodTest {

  Supplier<Long> currentTimeMillis = null;

  final SchedulerConfigStoreInMemory configStore = new SchedulerConfigStoreInMemory(() -> currentTimeMillis);

  private final SchedulerConfigStoreWithExtensions schedulerConfigStoreWithExtensions = new SchedulerConfigStoreWithExtensions() {
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

  @Test
  public void name() {
    assertThat(1).isEqualTo(2);
  }
}
