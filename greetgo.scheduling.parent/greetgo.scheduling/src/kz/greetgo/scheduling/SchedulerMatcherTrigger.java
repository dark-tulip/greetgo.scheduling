package kz.greetgo.scheduling;

import kz.greetgo.conf.ConfData;
import kz.greetgo.scheduling.context.ContentStore;
import kz.greetgo.scheduling.context.SchedulerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SchedulerMatcherTrigger extends AbstractTrigger {

  protected final SchedulerContext schedulerContext;
  private String initialSchedulerPattern, initialSchedulerPlace;
  private String fromConfigDescription = null;

  private final String configKeyName;

  public final Job job;

  public static SchedulerMatcherTrigger create(final Method method,
                                               final Object controller,
                                               final SchedulerContext schedulerContext) {

    if (method.getAnnotation(Scheduled.class) == null) return null;

    return new SchedulerMatcherTrigger(method, controller, schedulerContext);
  }

  protected SchedulerMatcherTrigger(final Method method, final Object controller, SchedulerContext schedulerContext) {
    this.schedulerContext = schedulerContext;
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
        String s = ((FromConfig) annotation).value().trim();
        if (s.length() == 0) throw new SchedulerException("No description: Description must be in "
            + FromConfig.class.getSimpleName() + ".value() of " + controller.getClass().getSimpleName()
            + "." + method.getName());
        fromConfigDescription = s;
        continue;
      }
    }

    if (initialSchedulerPattern == null) throw new IllegalArgumentException("No annotation "
        + Scheduled.class.getSimpleName() + " in method " + method.toGenericString());
  }

  private volatile SchedulerMatcher matcher = null;
  private volatile long lastCheckTime = 0;

  @Override
  public void reset() {
    if (!isResettable()) return;
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

    SchedulerMatcher localMatcher = this.matcher;

    if (localMatcher == null) return false;

    if (checkErrorFileToDelete && isResettable()) {
      schedulerContext.configError().delete();
      checkErrorFileToDelete = false;
    }

    final long now = now();

    {
      final boolean ret = localMatcher.match(lastCheckTime, now);
      lastCheckTime = now;
      return ret;
    }
  }

  private String pattern, place;

  private long schedulerStartedAt = 0;

  private long configFileLastModified = 0;

  protected long getLastModifiedOf(ContentStore contentStore) {
    return contentStore.lastModifiedMillis();
  }

  private void createMatcher() throws Exception {
    readPatternAndPlace();

    try {
      matcher = new SchedulerMatcher(pattern, schedulerStartedAt, place);
    } catch (LeftSchedulerPattern e) {

      if (!isResettable()) {
        alwaysReturnFalse = true;
        catchException(e);
        return;
      }
      final long lastModified = getLastModifiedOf(schedulerContext.configContent());
      if (lastModified > 0 && lastModified != configFileLastModified) {
        configFileLastModified = lastModified;
        writeToErrorFile(e);
        catchException(e);
      }
    }
  }

  private void catchException(LeftSchedulerPattern e) {
    if (schedulerContext.exceptionCatcher() != null) {
      schedulerContext.exceptionCatcher().catchException(e);
    } else {
      e.printStackTrace();
    }
  }

  private void writeToErrorFile(LeftSchedulerPattern e) {
    try {
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();
      PrintStream out = new PrintStream(bOut, false, "UTF-8");
      out.println("error message: " + e.message);
      out.println("pattern: " + e.pattern);
      out.println("place: " + e.place);
      out.println();
      e.printStackTrace(out);
      out.flush();
      schedulerContext.configError().setContent(bOut.toByteArray());
    } catch (UnsupportedEncodingException err) {
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

  @Override
  public boolean isResettable() {
    return fromConfigDescription != null;
  }

  private void readPatternAndPlace() throws Exception {
    if (!isResettable()) {
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
    if (!schedulerContext.configContent().exists()) return;

    ConfData confData = new ConfData();
    confData.readFromStream(new ByteArrayInputStream(schedulerContext.configContent().getContent()));

    pattern = confData.str(configKeyName);
    place = schedulerContext.configContent().placeInfo() + ", key: " + configKeyName;
  }

  private void writePatternToFile() throws Exception {
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    {
      PrintStream out = new PrintStream(bOut, false, "UTF-8");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      out.println();
      out.println("# (added at " + sdf.format(new Date()) + ")");
      for (String line : fromConfigDescription.split("\n")) {
        out.println("# " + line);
      }
      out.println(configKeyName + "=" + pattern);
      out.flush();
    }
    appendTo(schedulerContext.configContent(), bOut.toByteArray());
  }

  private static void appendTo(ContentStore contentStore, byte[] content) {
    if (content == null) return;
    if (content.length == 0) return;

    byte[] topContent = contentStore.getContent();
    if (topContent == null) {
      contentStore.setContent(content);
      return;
    }

    {
      byte[] fullContent = new byte[topContent.length + content.length];
      System.arraycopy(topContent, 0, fullContent, 0, topContent.length);
      System.arraycopy(content, 0, fullContent, topContent.length, content.length);
      contentStore.setContent(fullContent);
    }
  }
}
