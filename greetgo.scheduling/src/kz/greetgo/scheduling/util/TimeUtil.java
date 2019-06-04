package kz.greetgo.scheduling.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static kz.greetgo.scheduling.util.StrUtil.toLenZero;

public class TimeUtil {
  public static final long MILLIS_SECOND = 1000;
  public static final long MILLIS_MINUTE = MILLIS_SECOND * 60;
  public static final long MILLIS_HOUR = MILLIS_MINUTE * 60;

  public static long hmsToMillis(String hmsStr) {
    String[] split = hmsStr.split(":");

    if (split.length != 2 && split.length != 3) {
      throw new RuntimeException("Illegal TIME_OF_DAY value");
    }

    long hours = Long.parseLong(split[0]);
    long minutes = Long.parseLong(split[1]);
    long seconds = 0;
    if (split.length >= 3) {
      seconds = Long.parseLong(split[2]);
    }

    return longYmsToMillis(hours, minutes, seconds);
  }

  public static String millisToHms(long millis) {

    long hour = millis / MILLIS_HOUR;

    long restInHour = millis - hour * MILLIS_HOUR;

    long minute = restInHour / MILLIS_MINUTE;

    long restInMinute = restInHour - minute * MILLIS_MINUTE;

    long second = restInMinute / MILLIS_SECOND;

    return toLenZero(hour, 2) + ":" + toLenZero(minute, 2) + ":" + toLenZero(second, 2);
  }

  public static long longYmsToMillis(long hours, long minutes, long seconds) {
    return MILLIS_SECOND * seconds + MILLIS_MINUTE * minutes + MILLIS_HOUR * hours;
  }

  public static boolean isIntersect(long from1, long to1, long from2, long to2) {

    if (from1 > to1) {
      long tmp = from1;
      from1 = to1;
      to1 = tmp;
    }
    if (from2 > to2) {
      long tmp = from2;
      from2 = to2;
      to2 = tmp;
    }

    return to1 >= from2 && to2 >= from1;

  }

  /**
   * Переводит дату-время в количество миллисекунд прощедьшее с начала дня этой даты.
   *
   * @param dateTimeMillis Дата-время в миллисекундах (которая возвращается методом {@link Date#getTime()})
   * @return количество миллисекунд прощедьшее с начала дня этой даты.
   * Это число не может превышать следующую величину:
   * <pre>
   *   ЧАСОВ_В_СУТКАХ * МИНУТ_В_ЧАСЕ * СЕКУНД_В_МИНУТЕ * МИЛЛИСЕКУНД_В_СЕКУНДЕ = 24 * 60 * 60 * 1000 = 86400000
   * </pre>
   */
  public static long millisFromDayBegin(long dateTimeMillis) {
    Calendar calendar = new GregorianCalendar();

    calendar.setTimeInMillis(dateTimeMillis);

    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);
    int milli = calendar.get(Calendar.MILLISECOND);

    return longYmsToMillis(hour, minute, second) + milli;
  }

}
