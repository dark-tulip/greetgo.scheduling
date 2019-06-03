package kz.greetgo.scheduling.collector;

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
}
