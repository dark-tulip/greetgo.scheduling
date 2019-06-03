package kz.greetgo.scheduling.collector;

import kz.greetgo.scheduling.FromConfig;
import kz.greetgo.scheduling.Scheduled;

public class ScheduledDefinition {

  public final String name;
  public final String patternFromAnnotation;
  public final boolean isFromFile;
  public final String patternDescription;

  public ScheduledDefinition(String name, String patternFromAnnotation, boolean isFromFile, String patternDescription) {
    this.name = name;
    this.patternFromAnnotation = patternFromAnnotation;
    this.isFromFile = isFromFile;
    this.patternDescription = patternDescription;
  }

  public static ScheduledDefinition of(String methodName, Scheduled scheduled, FromConfig fromConfig) {

    if (fromConfig == null) {
      return new ScheduledDefinition(methodName, scheduled.value(), false, null);
    } else {
      return new ScheduledDefinition(methodName, scheduled.value(), true, fromConfig.value());
    }

  }

}
