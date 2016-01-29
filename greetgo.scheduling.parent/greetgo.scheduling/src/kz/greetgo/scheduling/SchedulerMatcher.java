package kz.greetgo.scheduling;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SchedulerMatcher {
  private final String pattern;
  private final String place;

  private boolean months[] = null;
  private boolean daysOfMonth[] = null;
  private boolean daysOfWeek[] = null;
  private boolean hours[] = null;
  private boolean minutes[] = null;

  public SchedulerMatcher(String pattern, String place) {
    if (pattern == null) throw new LeftSchedulerPattern("null pattern", null, place);

    this.pattern = pattern;
    this.place = place;

    boolean absent = true;
    for (String part : pattern.trim().split("\\s+")) {
      parsePatternPart(part);
      absent = false;
    }

    if (absent) throw new LeftSchedulerPattern("Absent parts", pattern, place);
  }


  public boolean match(long prevMatch, long now) {
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      if (sdf.format(new Date(prevMatch)).equals(sdf.format(new Date(now)))) return false;
    }

    GregorianCalendar c = new GregorianCalendar();
    c.setTimeInMillis(now);

    if (months != null) {
      final int month = c.get(Calendar.MONTH);
      if (!months[month]) return false;
    }

    if (daysOfMonth != null) {
      final int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
      if (!daysOfMonth[dayOfMonth - 1]) return false;
    }

    if (daysOfWeek != null) {
      final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);//1 - вос, 2 - пон, ..., 7 - суб
      if (!daysOfWeek[dayOfWeek - 1]) return false;
    }

    if (hours != null) {
      final int hour = c.get(Calendar.HOUR_OF_DAY);
      if (!hours[hour]) return false;
    }

    if (minutes != null) {
      final int minute = c.get(Calendar.MINUTE);
      if (!minutes[minute]) return false;
    }

    return true;
  }

  // 12/3:0/3 (03-07,11,14-18,21) [понедельник,вт,ср] [март-июнь]
  private void parsePatternPart(String part) {

    if (part.startsWith("(")) {
      parsePartDaysOfMonth(part);
      return;
    }

    if (part.startsWith("[")) {
      parsePartMonth(part);
      return;
    }

    if (part.startsWith("{")) {
      parsePartDayOfWeek(part);
      return;
    }

    parsePartHourAndMinute(part);

  }


  // (03-07,11,14-18,21)
  private void parsePartDaysOfMonth(String part) {
    if (!part.endsWith(")")) throw new LeftSchedulerPattern("No ) at the end of part of pattern", pattern, place);
    part = part.substring(1, part.length() - 1);
    for (String element : part.split(",")) {
      final int i = element.indexOf('-');
      if (i < 0) {
        appendDayOfMonthElements(element, element);
      } else {
        appendDayOfMonthElements(element.substring(0, i), element.substring(i + 1));
      }
    }
  }

  private void appendDayOfMonthElements(String fromStr, String toStr) {
    final int from = toInt(fromStr, 1, 31);
    final int to = toInt(toStr, 1, 31);

    if (daysOfMonth == null) {
      daysOfMonth = new boolean[31];
      Arrays.fill(daysOfMonth, false);
    }

    for (int i = from; i <= to; i++) {
      daysOfMonth[i - 1] = true;
    }
  }

  private int toInt(final String strInt, int limitFrom, int limitTo) {
    String tmp = strInt;
    while (tmp.length() > 1 && tmp.startsWith("0")) tmp = tmp.substring(1);

    final int ret;
    try {
      ret = Integer.parseInt(tmp);
    } catch (NumberFormatException e) {
      throw error("Не верный формат числа: [[" + strInt + "]]");
    }

    if (ret < limitFrom || limitTo < ret) {
      throw error("Value " + strInt + " is out of range (" + limitFrom + ", " + limitTo + ")");
    }

    return ret;
  }

  private LeftSchedulerPattern error(String message) {
    return new LeftSchedulerPattern(message, pattern, place);
  }


  // {понедельник,вт,ср}
  private void parsePartDayOfWeek(String part) {
    if (!part.endsWith("}")) throw new LeftSchedulerPattern("No } at the end of part of pattern", pattern, place);
    part = part.substring(1, part.length() - 1);

    for (String element : part.split(",")) {
      final int i = element.indexOf('-');
      if (i < 0) {
        appendDayOfWeekElements(element, element);
      } else {
        appendDayOfWeekElements(element.substring(0, i), element.substring(i + 1));
      }
    }
  }

  private void appendDayOfWeekElements(String fromStr, String toStr) {
    final int from = dayOfWeekToInt(fromStr, pattern, place);
    final int to = dayOfWeekToInt(toStr, pattern, place);

    if (daysOfWeek == null) {
      daysOfWeek = new boolean[7];
      Arrays.fill(daysOfWeek, false);
    }

    for (int i = from; i <= to; i++) {
      daysOfWeek[i - 1] = true;
    }
  }

  /**
   * Converts name of day of week to int
   *
   * @param dayOfWeek name of week
   * @param pattern   parsing pattern
   * @param place     the place where was got pattern
   * @return One of 1, 2, 3, 4, 5, 6, 7
   */
  static int dayOfWeekToInt(String dayOfWeek, String pattern, String place) {
    if (dayOfWeek == null) throw new IllegalArgumentException("dayOfWeek == null");

    dayOfWeek = dayOfWeek.toUpperCase();

    if ("ВС".equals(dayOfWeek)) return 1;
    if ("ПН".equals(dayOfWeek)) return 2;
    if ("ВТ".equals(dayOfWeek)) return 3;
    if ("СР".equals(dayOfWeek)) return 4;
    if ("ЧТ".equals(dayOfWeek)) return 5;
    if ("ПТ".equals(dayOfWeek)) return 6;
    if ("СБ".equals(dayOfWeek)) return 7;

    if (dayOfWeek.length() >= 3) {

      if (dayOfWeek.startsWith("ВОС")) return 1;
      if (dayOfWeek.startsWith("ПОН")) return 2;
      if (dayOfWeek.startsWith("ВТО")) return 3;
      if (dayOfWeek.startsWith("СРЕ")) return 4;
      if (dayOfWeek.startsWith("ЧЕТ")) return 5;
      if (dayOfWeek.startsWith("ПЯТ")) return 6;
      if (dayOfWeek.startsWith("СУБ")) return 7;

      if (dayOfWeek.startsWith("SUN")) return 1;
      if (dayOfWeek.startsWith("MON")) return 2;
      if (dayOfWeek.startsWith("TUE")) return 3;
      if (dayOfWeek.startsWith("WED")) return 4;
      if (dayOfWeek.startsWith("THU")) return 5;
      if (dayOfWeek.startsWith("FRI")) return 6;
      if (dayOfWeek.startsWith("SAT")) return 7;

    }

    throw new LeftSchedulerPattern("Unknown name of day of week = " + dayOfWeek, pattern, place);
  }

  // [март-июнь]
  private void parsePartMonth(String part) {
    if (!part.endsWith("]")) throw new LeftSchedulerPattern("No ] at the end of part of pattern", pattern, place);
    part = part.substring(1, part.length() - 1);

    for (String element : part.split(",")) {
      final int i = element.indexOf('-');
      if (i < 0) {
        appendMonthElements(element, element);
      } else {
        appendMonthElements(element.substring(0, i), element.substring(i + 1));
      }
    }
  }

  private void appendMonthElements(String fromStr, String toStr) {
    final int from = monthToInt(fromStr, pattern, place);
    final int to = monthToInt(toStr, pattern, place);

    if (months == null) {
      months = new boolean[12];
      Arrays.fill(months, false);
    }

    for (int i = from; i <= to; i++) {
      months[i - 1] = true;
    }
  }

  /**
   * Converts name of month to int
   *
   * @param monthName name of month
   * @param pattern   parsing pattern
   * @param place     the place where was got pattern
   * @return One of 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
   */
  static int monthToInt(String monthName, String pattern, String place) {
    if (monthName == null) throw new IllegalArgumentException("monthName == null");
    monthName = monthName.toUpperCase();

    if (monthName.length() >= 3) {

      if (monthName.startsWith("ЯНВ")) return 1;
      if (monthName.startsWith("ФЕВ")) return 2;
      if (monthName.startsWith("МАР")) return 3;
      if (monthName.startsWith("АПР")) return 4;
      if (monthName.startsWith("МАЙ")) return 5;
      if (monthName.startsWith("ИЮН")) return 6;
      if (monthName.startsWith("ИЮЛ")) return 7;
      if (monthName.startsWith("АВГ")) return 8;
      if (monthName.startsWith("СЕН")) return 9;
      if (monthName.startsWith("ОКТ")) return 10;
      if (monthName.startsWith("НОЯ")) return 11;
      if (monthName.startsWith("ДЕК")) return 12;
      if (monthName.startsWith("JAN")) return 1;
      if (monthName.startsWith("FEB")) return 2;
      if (monthName.startsWith("MAR")) return 3;
      if (monthName.startsWith("APR")) return 4;
      if (monthName.startsWith("MAY")) return 5;
      if (monthName.startsWith("JUN")) return 6;
      if (monthName.startsWith("JUL")) return 7;
      if (monthName.startsWith("AUG")) return 8;
      if (monthName.startsWith("SEP")) return 9;
      if (monthName.startsWith("OCT")) return 10;
      if (monthName.startsWith("NOV")) return 11;
      if (monthName.startsWith("DEC")) return 12;

    }

    throw new LeftSchedulerPattern("Unknown name of month = " + monthName, pattern, place);
  }

  // 12/3:0/3
  private void parsePartHourAndMinute(String part) {

  }

}
