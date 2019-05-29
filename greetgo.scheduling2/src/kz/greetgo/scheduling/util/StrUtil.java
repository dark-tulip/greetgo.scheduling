package kz.greetgo.scheduling.util;

public class StrUtil {

  public static String toLenZero(int number, int len) {
    StringBuilder sb = new StringBuilder();
    sb.append(number);
    while (sb.length() < len) {
      sb.insert(0, '0');
    }
    return sb.toString();
  }

}
