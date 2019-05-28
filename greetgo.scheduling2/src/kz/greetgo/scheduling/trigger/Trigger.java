package kz.greetgo.scheduling.trigger;

import java.util.Date;

public interface Trigger {

  boolean isHit(Date periodFrom, Date periodTo);

  boolean isDotty();

}
