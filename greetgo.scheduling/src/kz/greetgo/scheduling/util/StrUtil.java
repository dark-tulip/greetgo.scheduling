package kz.greetgo.scheduling.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StrUtil {

  public static String toLenZero(long number, int len) {
    StringBuilder sb = new StringBuilder();
    sb.append(number);
    while (sb.length() < len) {
      sb.insert(0, '0');
    }
    return sb.toString();
  }

  public static String toLenSpace(Object object, int len) {
    StringBuilder sb = new StringBuilder();
    sb.append(object);
    while (sb.length() < len) {
      sb.insert(0, ' ');
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

  public static String streamToStr(InputStream inputStream) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024 * 4];
      while (true) {
        int count = inputStream.read(buffer);
        if (count < 0) {
          return out.toString("UTF-8");
        }
        out.write(buffer, 0, count);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
