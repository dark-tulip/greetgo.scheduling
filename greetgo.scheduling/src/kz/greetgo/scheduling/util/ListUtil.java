package kz.greetgo.scheduling.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtil {
  public static <T> List<T> concatLists(List<T> list1, List<T> list2) {

    int size1 = list1.size();
    int size2 = list2.size();

    if (size1 == 0) {
      if (size2 == 0) {
        return Collections.emptyList();
      }
      return list2;
    }

    if (size2 == 0) {
      return list1;
    }

    {
      List<T> ret = new ArrayList<>(size1 + size2);
      ret.addAll(list1);
      ret.addAll(list2);
      return ret;
    }

  }

}
