package kz.greetgo.scheduling;

public class LeftSchedulerPattern extends SchedulerException {
  public final String message;
  public final String pattern;
  public final String place;

  public LeftSchedulerPattern(String message, String pattern, String place) {
    super(message + " : [[" + pattern + "]] in " + place);
    this.message = message;
    this.pattern = pattern;
    this.place = place;
  }
}
