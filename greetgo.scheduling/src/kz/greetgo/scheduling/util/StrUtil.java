package kz.greetgo.scheduling.util;

public class StrUtil {

  public static String toLenZero(long number, int len) {
    StringBuilder sb = new StringBuilder();
    sb.append(number);
    while (sb.length() < len) {
      sb.insert(0, '0');
    }
    return sb.toString();
  }

  public static String mul(String s, int times) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < times; i++) {
      sb.append(s);
    }
    return sb.toString();
  }
}
