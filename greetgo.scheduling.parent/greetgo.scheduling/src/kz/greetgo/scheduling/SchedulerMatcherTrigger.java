package kz.greetgo.scheduling;

import kz.greetgo.conf.ConfData;
import kz.greetgo.scheduling.annotations.FromConfig;
import kz.greetgo.scheduling.annotations.Scheduled;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kz.greetgo.util.ServerUtil.dummyCheck;

public class SchedulerMatcherTrigger extends AbstractTrigger {

  private final File configFile;
  private String initialSchedulerPattern, initialSchedulerPlace;
  private String fromConfigDescription = null;

  private final String configKeyName;

  public final Job job;

  public static SchedulerMatcherTrigger create(final Method method, final Object controller, File configFile) {

    if (method.getAnnotation(Scheduled.class) == null) return null;

    return new SchedulerMatcherTrigger(method, controller, configFile);
  }

  protected SchedulerMatcherTrigger(final Method method, final Object controller, File configFile) {
    this.configFile = configFile;
    configKeyName = method.getName();

    job = new Job() {
      @Override
      public void doWork() throws Throwable {
        method.invoke(controller);
      }

      @Override
      public String infoForError() {
        return "Scheduled method " + controller.getClass().getSimpleName() + "." + method.getName();
      }
    };

    initialSchedulerPattern = null;

    for (Annotation annotation : method.getAnnotations()) {
      if (annotation instanceof Scheduled) {
        initialSchedulerPattern = ((Scheduled) annotation).value();
        initialSchedulerPlace = "@" + Scheduled.class.getSimpleName() + " "
          + controller.getClass().getSimpleName() + "." + method.getName();
        continue;
      }
      if (annotation instanceof FromConfig) {
        fromConfigDescription = ((FromConfig) annotation).value().trim();
        if (fromConfigDescription.length() == 0) {
          throw new SchedulerException("No description: Description must be in " + FromConfig.class.getSimpleName()
            + ".value() of " + controller.getClass().getSimpleName() + "." + method.getName());
        }
        //noinspection UnnecessaryContinue
        continue;
      }
    }

    if (initialSchedulerPattern == null) {
      throw new IllegalArgumentException("No annotation " + Scheduled.class.getSimpleName()
        + " in method " + method.toGenericString());
    }
  }

  private SchedulerMatcher matcher = null;
  private long lastCheckTime = 0;

  public void reset() {
    matcher = null;
    lastCheckTime = 0;
  }

  protected long now() {
    return System.currentTimeMillis();
  }

  @Override
  public boolean isItTimeToRun() {
    if (matcher == null) try {
      createMatcher();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    final long now = now();

    {
      final boolean ret = matcher.match(lastCheckTime, now);
      lastCheckTime = now;
      return ret;
    }
  }

  private String pattern, place;

  private Long schedulerStartedAt = null;

  private void createMatcher() throws Exception {
    readPatternAndPlace();

    matcher = new SchedulerMatcher(pattern, schedulerStartedAt, place);
  }

  @Override
  public void schedulerIsStartedJustNow() {
    schedulerStartedAt = now();
  }

  @Override
  public void jobIsGoingToStart() {
    if (matcher != null) matcher.taskStartedAt(now());
  }

  @Override
  public void jobHasFinishedJustNow() {
    if (matcher != null) matcher.taskFinishedAt(now());
  }

  private void readPatternAndPlace() throws Exception {
    if (fromConfigDescription == null) {
      pattern = initialSchedulerPattern;
      place = initialSchedulerPlace;
      return;
    }

    pattern = place = null;

    readPatternAndPlaceFromFile();

    if (pattern != null) return;

    pattern = initialSchedulerPattern;
    place = initialSchedulerPlace;

    writePatternToFile();
  }

  private void readPatternAndPlaceFromFile() throws Exception {
    if (!configFile.exists()) return;

    ConfData confData = new ConfData();
    confData.readFromFile(configFile);

    pattern = confData.str(configKeyName);
    place = "File " + configFile.getName() + ", key: " + configKeyName;
  }

  private void writePatternToFile() throws Exception {

    if (!configFile.exists()) dummyCheck(configFile.getParentFile().mkdirs());

    try (final PrintStream out = new PrintStream(new FileOutputStream(configFile, true), false, "UTF-8")) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      out.println();
      out.println("# (added at " + sdf.format(new Date()) + ")");
      for (String line : fromConfigDescription.split("\n")) {
        out.println("# " + line);
      }
      out.println(configKeyName + "=" + pattern);
    }

  }
}
