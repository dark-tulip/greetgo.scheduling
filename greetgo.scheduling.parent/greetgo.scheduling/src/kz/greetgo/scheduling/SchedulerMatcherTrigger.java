package kz.greetgo.scheduling;

import kz.greetgo.conf.ConfData;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kz.greetgo.util.ServerUtil.dummyCheck;

public class SchedulerMatcherTrigger extends AbstractTrigger {

  private final File configFile, errorFile;
  private String initialSchedulerPattern, initialSchedulerPlace;
  private String fromConfigDescription = null;

  private final String configKeyName;

  public final Job job;
  public ExceptionCatcher exceptionCatcher = new ExceptionCatcher() {
    @Override
    public void catchException(Exception e) {
      e.printStackTrace();
    }
  };

  public static SchedulerMatcherTrigger create(final Method method, final Object controller, File configFile) {

    if (method.getAnnotation(Scheduled.class) == null) return null;

    return new SchedulerMatcherTrigger(method, controller, configFile);
  }

  protected SchedulerMatcherTrigger(final Method method, final Object controller, File configFile) {
    this.configFile = configFile;
    errorFile = new File(configFile.getPath() + ".error");
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
        continue;
      }
    }

    if (initialSchedulerPattern == null) {
      throw new IllegalArgumentException("No annotation " + Scheduled.class.getSimpleName()
          + " in method " + method.toGenericString());
    }
  }

  private volatile SchedulerMatcher matcher = null;
  private volatile long lastCheckTime = 0;

  public void reset() {
    matcher = null;
    checkErrorFileToDelete = true;
  }

  protected long now() {
    return System.currentTimeMillis();
  }

  private boolean alwaysReturnFalse = false;

  private boolean checkErrorFileToDelete = false;

  @Override
  public boolean isItTimeToRun() {
    if (alwaysReturnFalse) return false;

    if (matcher == null) try {
      createMatcher();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (matcher == null) return false;

    if (checkErrorFileToDelete && !noConfigFile()) {
      dummyCheck(errorFile.delete());
      checkErrorFileToDelete = false;
    }

    final long now = now();

    {
      final boolean ret = matcher.match(lastCheckTime, now);
      lastCheckTime = now;
      return ret;
    }
  }

  private String pattern, place;

  private long schedulerStartedAt = 0;

  private long configFileLastModified = 0;

  protected long getLastModifiedOf(File file) {
    return file.lastModified();
  }

  private void createMatcher() throws Exception {
    readPatternAndPlace();

    try {
      matcher = new SchedulerMatcher(pattern, schedulerStartedAt, place);
    } catch (LeftSchedulerPattern e) {

      if (noConfigFile()) {
        alwaysReturnFalse = true;
        catchException(e);
        return;
      }
      final long lastModified = getLastModifiedOf(configFile);
      if (lastModified > 0 && lastModified != configFileLastModified) {
        configFileLastModified = lastModified;
        writeToErrorFile(e);
        catchException(e);
      }
    }
  }

  private void catchException(LeftSchedulerPattern e) {
    if (exceptionCatcher != null) {
      exceptionCatcher.catchException(e);
    } else {
      e.printStackTrace();
    }
  }

  private void writeToErrorFile(LeftSchedulerPattern e) {
    try (final PrintStream out = new PrintStream(errorFile, "UTF-8")) {
      out.println("error message: " + e.message);
      out.println("pattern: " + e.pattern);
      out.println("place: " + e.place);
      out.println();
      e.printStackTrace(out);
    } catch (FileNotFoundException | UnsupportedEncodingException err) {
      throw new RuntimeException(err);
    }
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

  @Override
  public void markThatInExecutionQueue() {
    if (matcher != null) matcher.taskFellInExecutionQueueAt(now());
  }

  private boolean noConfigFile() {
    return fromConfigDescription == null;
  }

  private void readPatternAndPlace() throws Exception {
    if (noConfigFile()) {
      pattern = initialSchedulerPattern;
      place = initialSchedulerPlace;
      return;
    }

    pattern = place = null;

    readPatternAndPlaceFromConfig();

    if (pattern != null) return;

    pattern = initialSchedulerPattern;
    place = initialSchedulerPlace;

    writePatternToFile();
  }

  private void readPatternAndPlaceFromConfig() throws Exception {
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
