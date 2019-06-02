package kz.greetgo.scheduling.collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SupplierSeq<T> implements Supplier<T> {

  private final List returns = new ArrayList<>();
  private final AtomicInteger index = new AtomicInteger(0);

  private SupplierSeq() {}

  @SuppressWarnings({"unchecked"})
  public static <T> SupplierSeq<T> of(Object... array) {
    SupplierSeq<T> ret = new SupplierSeq<>();
    Collections.addAll(ret.returns, array);
    return ret;
  }

  @Override
  public T get() {
    //noinspection unchecked
    return (T) returns.get(index.getAndIncrement());
  }

}
