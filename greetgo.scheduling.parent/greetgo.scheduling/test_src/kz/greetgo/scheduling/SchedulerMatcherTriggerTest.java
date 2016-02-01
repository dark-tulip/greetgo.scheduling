package kz.greetgo.scheduling;

import kz.greetgo.conf.ConfData;
import kz.greetgo.scheduling.annotations.FromConfig;
import kz.greetgo.scheduling.annotations.Scheduled;
import kz.greetgo.util.RND;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static kz.greetgo.util.ServerUtil.dummyCheck;
import static org.fest.assertions.api.Assertions.assertThat;

public class SchedulerMatcherTriggerTest {
  private static final String WORK_DIR = "build/" + SchedulerMatcherTriggerTest.class.getSimpleName() + "/";

  static class MySchedulerMatcherTrigger extends SchedulerMatcherTrigger {
    public MySchedulerMatcherTrigger(Method method, Object controller, File configFile) {
      super(method, controller, configFile);
    }

    long now;

    @Override
    protected long now() {
      return now;
    }
  }

  static class TestControllerNoConfig {

    boolean forTestWasCalled = false;

    @Scheduled("13:00")
    @SuppressWarnings("unused")
    public void forTest() {
      forTestWasCalled = true;
    }

  }

  @Test
  public void reset_isItTimeToRun_noConfig() throws Exception {

    final TestControllerNoConfig controller = new TestControllerNoConfig();
    final Method method = controller.getClass().getMethod("forTest");

    final MySchedulerMatcherTrigger t
      = new MySchedulerMatcherTrigger(method, controller, new File(WORK_DIR + "leftFile"));

    t.now = at("2015-02-01 11:00:00");

    //
    //
    t.reset();
    //
    //

    t.now = at("2015-02-01 13:00:04");

    //
    //
    assertThat(t.isItTimeToRun()).isTrue();
    assertThat(t.isItTimeToRun()).isFalse();
    //
    //

    t.now = at("2015-02-02 13:00:05");

    //
    //
    assertThat(t.isItTimeToRun()).isTrue();
    assertThat(t.isItTimeToRun()).isFalse();
    //
    //

    assertThat(controller.forTestWasCalled).isFalse();

    try {
      t.job.doWork();
    } catch (Throwable throwable) {
      if (throwable instanceof Exception) throw (Exception) throwable;
      throw new Exception(throwable);
    }

    assertThat(controller.forTestWasCalled).isTrue();
  }

  private static long at(String timeAsStr) throws ParseException {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.parse(timeAsStr).getTime();
  }

  static class TestControllerWithConfig {

    boolean forTestWasCalled = false;

    @SuppressWarnings("unused")
    @Scheduled("13:00")
    @FromConfig("Это описание задачи\nв двух строках")
    public void forTest() {
      forTestWasCalled = true;
    }

  }

  @Test
  public void reset_isItTimeToRun_withConfig_checkFileCreating() throws Exception {

    File configFile = new File(WORK_DIR + "reset_isItTimeToRun_withConfig_checkFileCreating"
      + "_" + RND.intStr(5) + ".config.txt");

    final TestControllerWithConfig controller = new TestControllerWithConfig();
    final Method method = controller.getClass().getMethod("forTest");

    final MySchedulerMatcherTrigger t = new MySchedulerMatcherTrigger(method, controller, configFile);

    t.now = at("2015-02-01 11:00:00");

    //
    //
    t.reset();
    t.isItTimeToRun();
    //
    //

    assertThat(configFile).exists();

    ConfData confData = new ConfData();
    confData.readFromFile(configFile);

    assertThat(confData.str("forTest")).isEqualTo("13:00");

  }

  @Test
  public void reset_isItTimeToRun_withConfig_checkFileReading() throws Exception {

    File configFile = new File(WORK_DIR + "reset_isItTimeToRun_withConfig_checkFileReading"
      + "_" + RND.intStr(5) + ".config.txt");

    {
      dummyCheck(configFile.getParentFile().mkdirs());
      try (PrintStream out = new PrintStream(configFile, "UTF-8")) {
        out.println("forTest=14:00");
      }
    }

    final TestControllerWithConfig controller = new TestControllerWithConfig();
    final Method method = controller.getClass().getMethod("forTest");

    final MySchedulerMatcherTrigger t = new MySchedulerMatcherTrigger(method, controller, configFile);

    t.now = at("2015-02-01 11:00:00");

    //
    //
    t.reset();
    //
    //

    t.now = at("2015-02-01 13:00:00");

    //
    //
    assertThat(t.isItTimeToRun()).isFalse();
    //
    //

    t.now = at("2015-02-01 14:00:00");

    //
    //
    assertThat(t.isItTimeToRun()).isTrue();
    assertThat(t.isItTimeToRun()).isFalse();
    //
    //

  }

}