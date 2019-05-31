package kz.greetgo.scheduling;

import kz.greetgo.conf.ConfData;
import kz.greetgo.scheduling.context.ContentStore;
import kz.greetgo.scheduling.context.SchedulerContextOnFile;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static kz.greetgo.scheduling.SchedulerMatcherTrigger.ensureExistsKeyValue;
import static kz.greetgo.util.ServerUtil.dummyCheck;
import static org.fest.assertions.api.Assertions.assertThat;

public class SchedulerMatcherTriggerTest {
  private static final String WORK_DIR = "build/" + SchedulerMatcherTriggerTest.class.getSimpleName() + "/";

  static class MySchedulerMatcherTrigger extends SchedulerMatcherTrigger {
    public MySchedulerMatcherTrigger(Method method, Object controller, File configFile) {
      super(method, controller, new SchedulerContextOnFile(configFile));
    }

    long now;

    @Override
    protected long now() {
      return now;
    }

    long returnValueInGetLastModifiedOf = 0;

    @Override
    protected long getLastModifiedOf(ContentStore contentStore) {
      return returnValueInGetLastModifiedOf;
    }

    public void setExceptionCatcher(TestExceptionCatcher tec) {
      ((SchedulerContextOnFile) schedulerContext).exceptionCatcher = tec;
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
      + "_" + RND.strInt(5) + ".config.txt");

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
      + "_" + RND.strInt(5) + ".config.txt");

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

  private static void setLineToFile(MySchedulerMatcherTrigger t, File configFile, String line) throws Exception {
    try (final PrintStream out = new PrintStream(configFile, "UTF-8")) {
      out.println(line);
    }

    t.returnValueInGetLastModifiedOf++;
  }

  private static class TestExceptionCatcher implements ExceptionCatcher {

    final List<Exception> caughtExceptions = new ArrayList<>();

    @Override
    public void catchException(Exception e) {
      caughtExceptions.add(e);
    }

    void clean() {
      caughtExceptions.clear();
    }
  }

  static class ForTestProcessingOfPatternFormatErrorsFromFile {
    @Scheduled("13:00")
    @FromConfig("описание")
    @SuppressWarnings("unused")
    public void forTest() {
    }
  }

  @Test
  public void testProcessingOfPatternFormatErrorsFromFile() throws Exception {
    File configFile = new File(WORK_DIR + "testProcessingOfPatternFormatErrorsFromFile"
      + "_" + RND.strInt(5) + ".config.txt");
    dummyCheck(configFile.getParentFile().mkdirs());

    File errorFile = new File(configFile.getPath() + ".error");

    TestExceptionCatcher tec = new TestExceptionCatcher();

    ForTestProcessingOfPatternFormatErrorsFromFile controller = new ForTestProcessingOfPatternFormatErrorsFromFile();
    final Method method = controller.getClass().getMethod("forTest");
    final MySchedulerMatcherTrigger t = new MySchedulerMatcherTrigger(method, controller, configFile);
    t.setExceptionCatcher(tec);
    t.now = at("2015-02-01 11:00:00");

    setLineToFile(t, configFile, "forTest=абра кадабра всякая");

    t.reset();
    t.isItTimeToRun();

    assertThat(errorFile).exists();

    dummyCheck(errorFile.delete());

    assertThat(tec.caughtExceptions).hasSize(1);
    tec.clean();

    t.isItTimeToRun();

    assertThat(errorFile).as("Система должна понять, что файл не менялся," +
      " и поэтому второй раз файл ошибки создаваться не должен").doesNotExist();

    assertThat(tec.caughtExceptions).as("Система должна понять, что файл не менялся," +
      " и поэтому второй раз exception кидаться не должен").isEmpty();

    setLineToFile(t, configFile, "forTest=абра кадабра всякая, но уже другая");

    t.isItTimeToRun();

    assertThat(errorFile).as("Так как файл конфига поменялся, то ошибка вылететь обязана").exists();

    assertThat(tec.caughtExceptions).hasSize(1);
    tec.clean();

    setLineToFile(t, configFile, "forTest=13:00");

    t.isItTimeToRun();

    assertThat(errorFile).as("Конфиг стал без ошибки и файл с ошибкой должен быть удалён").doesNotExist();

    assertThat(tec.caughtExceptions).isEmpty();

    setLineToFile(t, configFile, "forTest=14:00");

    t.isItTimeToRun();

    assertThat(errorFile).as("Ну так! на всякий случай").doesNotExist();

    assertThat(tec.caughtExceptions).isEmpty();

    dummyCheck(configFile.delete());//подчистим за собой
  }

  static class ForTestProcessingOfPatternFormatErrorsNoFile {
    @Scheduled("абра кадабра всякая")
    @SuppressWarnings("unused")
    public void forTest() {
    }
  }

  @Test
  public void testProcessingOfPatternFormatErrorsNoFile() throws Exception {
    File configFile = new File(WORK_DIR + "testProcessingOfPatternFormatErrorsNoFile"
      + "_" + RND.strInt(5) + ".config.txt");

    File errorFile = new File(configFile.getPath() + ".error");

    TestExceptionCatcher tec = new TestExceptionCatcher();

    ForTestProcessingOfPatternFormatErrorsNoFile controller = new ForTestProcessingOfPatternFormatErrorsNoFile();
    final Method method = controller.getClass().getMethod("forTest");
    final MySchedulerMatcherTrigger t = new MySchedulerMatcherTrigger(method, controller, configFile);
    t.setExceptionCatcher(tec);
    t.now = at("2015-02-01 11:00:00");

    t.reset();
    t.isItTimeToRun();

    assertFilesDoNotExist(configFile, errorFile);

    assertThat(tec.caughtExceptions).hasSize(1);
    tec.clean();

    t.isItTimeToRun();

    assertFilesDoNotExist(configFile, errorFile);

    assertThat(tec.caughtExceptions).as("Нужно выкидывать ошибку только один раз").isEmpty();

  }

  private void assertFilesDoNotExist(File configFile, File errorFile) {
    assertThat(errorFile)
      .as("Если расписание берётся не из файла, то никакие файлы создаваться не должны")
      .doesNotExist();

    assertThat(configFile)
      .as("Если расписание берётся не из файла, то никакие файлы создаваться не должны")
      .doesNotExist();
  }

  @Test
  public void ensureExistsKeyValue_1() {

    String content = "asd_key1=wow\n" +
      "#asd_key1.m1=wow1\n";

    //
    //
    byte[] newContentBytes = ensureExistsKeyValue(content.getBytes(UTF_8), "asd_key1", "asd_key1.m1");
    //
    //

    String newContent = new String(newContentBytes, UTF_8);

    assertThat(newContent).isEqualTo(content);

  }

  @Test
  public void ensureExistsKeyValue_2() {

    String content = "asd_key1=wow\n"
      + "#asd_key1.m1=wow1\n"
      + "\n"
      + "moon\n\n    \t  \n \n";
    String expectedContent = "asd_key1=wow\n"
      + "#asd_key1.m1=wow1\n"
      + "#asd_key1.m2=wow\n"
      + "\n"
      + "moon\n";

    //
    //
    byte[] newContentBytes = ensureExistsKeyValue(content.getBytes(UTF_8), "asd_key1", "asd_key1.m2");
    //
    //

    String newContent = new String(newContentBytes, UTF_8);

    assertThat(newContent).isEqualTo(expectedContent);

  }

  @Test
  public void ensureExistsKeyValue_3() {

    String content = "asd_key1=wow\n"
      + "#asd_key1.m1=wow1\n"
      + "\n"
      + "moon\n";

    //
    //
    byte[] newContentBytes = ensureExistsKeyValue(content.getBytes(UTF_8), "dsa_key2", "dsa_key2.m2");
    //
    //

    String newContent = new String(newContentBytes, UTF_8);

    assertThat(newContent).isEqualTo(content);
  }
}