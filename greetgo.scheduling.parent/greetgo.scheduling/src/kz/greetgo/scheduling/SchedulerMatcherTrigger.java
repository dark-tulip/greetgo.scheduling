package kz.greetgo.scheduling;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kz.greetgo.conf.ConfData;
import kz.greetgo.conf.NoValue;
import kz.greetgo.scheduling.context.ContentStore;
import kz.greetgo.scheduling.context.SchedulerContext;

public class SchedulerMatcherTrigger extends AbstractTrigger {
  
  protected final SchedulerContext schedulerContext;
  private String initialSchedulerPattern, initialSchedulerPlace;
  private String fromConfigDescription = null;
  
  private final String configKeyName;
  
  public final Job job;
  
  public static SchedulerMatcherTrigger create(final Method method, final Object controller,
      final SchedulerContext schedulerContext) {
    
    if (method.getAnnotation(Scheduled.class) == null) return null;
    
    return new SchedulerMatcherTrigger(method, controller, schedulerContext);
  }
  
  protected SchedulerMatcherTrigger(final Method method, final Object controller,
      SchedulerContext schedulerContext) {
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
        initialSchedulerPattern = ((Scheduled)annotation).value();
        initialSchedulerPlace = "@" + Scheduled.class.getSimpleName() + " "
            + controller.getClass().getSimpleName() + "." + method.getName();
        continue;
      }
      if (annotation instanceof FromConfig) {
        String s = ((FromConfig)annotation).value().trim();
        if (s.length() == 0) throw new SchedulerException("No description: Description must be in "
            + FromConfig.class.getSimpleName() + ".value() of "
            + controller.getClass().getSimpleName() + "." + method.getName());
        fromConfigDescription = s;
        continue;
      }
    }
    
    if (initialSchedulerPattern == null) throw new IllegalArgumentException("No annotation "
        + Scheduled.class.getSimpleName() + " in method " + method.toGenericString());
  }
  
  private volatile long lastCheckTime = 0;
  
  @Override
  public String toString() {
    return "TRIGGER [" + pattern + "] " + matcher;
  }
  
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
  
  private long configFileLastModified = 0;
  
  protected long getLastModifiedOf(ContentStore contentStore) {
    return contentStore.lastModifiedMillis();
  }
  
  private volatile SchedulerMatcher matcher = null, prevMatcher = null;
  
  private void createMatcher() throws Exception {
    readPatternAndPlace();
    ensureMachineIdKey();
    
    try {
      
      SchedulerMatcher m = new SchedulerMatcher(pattern, place, taskRunStatus);
      
      matcher = m.equals(prevMatcher) ? prevMatcher :m;
      
      prevMatcher = matcher;
      
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
    taskRunStatus.schedulerStartedAt.set(now());
  }
  
  @Override
  public void jobIsGoingToStart() {
    SchedulerMatcher localMatcher = matcher;
    if (localMatcher != null) localMatcher.taskStartedAt(now());
  }
  
  private final TaskRunStatus taskRunStatus = new TaskRunStatus();
  
  @Override
  public TaskRunStatus getTaskRunStatus() {
    return taskRunStatus;
  }
  
  @Override
  public void jobHasFinishedJustNow() {
    SchedulerMatcher localMatcher = matcher;
    if (localMatcher != null) localMatcher.taskFinishedAt(now());
  }
  
  @Override
  public void markThatInExecutionQueue() {
    SchedulerMatcher localMatcher = matcher;
    if (localMatcher != null) localMatcher.taskFellInExecutionQueueAt(now());
  }
  
  @Override
  public boolean isResettable() {
    return fromConfigDescription != null;
  }
  
  private void ensureMachineIdKey() throws Exception {
    String machineId = schedulerContext.machineId();
    if (machineId == null) return;
    if (!isResettable()) return;
    
    byte[] content = schedulerContext.configContent().getContent();
    if (content == null) return;
    
    byte[] newContent = ensureExistsKeyValue(content, configKeyName,
        keyWithMachine(configKeyName, machineId));
    
    if (Arrays.equals(content, newContent)) return;
    
    schedulerContext.configContent().setContent(newContent);
  }
  
  private static String keyWithMachine(String configKeyName, String machineId) {
    return configKeyName + '.' + machineId;
  }
  
  private static final Pattern KEY_VALUE = Pattern.compile("\\s*#*\\s*([\\w\\.]+)\\s*=(.*)");
  
  static byte[] ensureExistsKeyValue(byte[] content, String topKey, String key) throws Exception {
    ArrayList<String> list = new ArrayList<>();
    {
      Collections.addAll(list, new String(content, "UTF-8").replaceAll("\r", "").split("\n"));
    }
    {
      int topKeyIndex = -1, lastKeyIndex = -1;
      String value = null;
      for (int i = 0, n = list.size(); i < n; i++) {
        Matcher matcher = KEY_VALUE.matcher(list.get(i));
        if (!matcher.matches()) continue;
        String currentKey = matcher.group(1);
        
        if (currentKey.equals(key)) return content;
        
        if (topKeyIndex < 0) {
          if (currentKey.equals(topKey)) {
            topKeyIndex = i;
            value = matcher.group(2);
          }
          continue;
        }
        
        if (currentKey.startsWith(topKey + '.')) {
          lastKeyIndex = i;
        }
      }
      
      if (topKeyIndex < 0) return content;
      
      {
        int index = lastKeyIndex;
        if (index < 0) index = topKeyIndex;
        list.add(index + 1, "#" + key + "=" + value);
      }
    }
    {
      while (list.size() > 0) {
        String lastLine = list.get(list.size() - 1);
        if (lastLine != null && lastLine.trim().length() > 0) break;
        list.remove(list.size() - 1);
      }
    }
    {
      StringBuilder sb = new StringBuilder();
      for (String line : list) {
        sb.append(line).append('\n');
      }
      return sb.toString().getBytes("UTF-8");
    }
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
    confData.readFromByteArray(schedulerContext.configContent().getContent());
    
    String key = configKeyName;
    if (schedulerContext.machineId() == null) {
      pattern = confData.str(key);
    } else
      try {
        key = keyWithMachine(configKeyName, schedulerContext.machineId());
        pattern = confData.strEx(key);
        if (pattern == null) throw new NoValue(key);
      } catch (NoValue ignore) {
        key = configKeyName;
        pattern = confData.str(key);
      }
    
    place = schedulerContext.configContent().placeInfo() + ", key: " + key;
  }
  
  private void writePatternToFile() throws Exception {
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();
    
    {
      PrintStream out = new PrintStream(bOut, false, "UTF-8");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      out.println();
      out.println("# (Added at " + sdf.format(new Date()) + ")");
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
