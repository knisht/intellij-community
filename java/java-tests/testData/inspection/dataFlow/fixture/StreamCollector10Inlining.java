package java.util.stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

// Mock
class Collectors {
  public static <T> Collector<T, ?, List<T>> toList() {<error descr="Missing return statement">}</error>
  public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {<error descr="Missing return statement">}</error>
  public static <T> Collector<T, ?, Set<T>> toUnmodifiableSet() {<error descr="Missing return statement">}</error>
  public static <T, K, U> Collector<T, ?, Map<K,U>> toUnmodifiableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {<error descr="Missing return statement">}</error>
}

public class StreamCollector10Inlining {

  @Nullable
  final String convert(String s) {
    return s.isEmpty() ? null : s;
  }

  void testToList() {
    List<String> list = Stream.of("foo", "bar", "baz").map(this::convert).collect(Collectors.toList());
    list.sort(null);
  }

  void testToImmutableList() {
    List<String> list = Stream.of("foo", "bar", "baz")
                              .map(<warning descr="Function may return null, but it's not allowed here">this::convert</warning>)
                              .collect(Collectors.toUnmodifiableList());
    list.<warning descr="Immutable object is modified">sort</warning>(null);
  }

  void testToImmutableSet() {
    Set<String> set = Stream.of("foo", "bar", "baz")
                            .map(<warning descr="Function may return null, but it's not allowed here">this::convert</warning>)
                            .collect(Collectors.toUnmodifiableSet());
    set.<warning descr="Immutable object is modified">add</warning>("qux");
  }

  void testToImmutableMap() {
    Map<String, String> map = Stream.of("foo", "bar", "baz", "")
      .collect(Collectors.toUnmodifiableMap(<warning descr="Function may return null, but it's not allowed here">this::convert</warning>, <warning descr="Function may return null, but it's not allowed here">this::convert</warning>));
    map.<warning descr="Immutable object is modified">put</warning>("qux", "qux");
  }
}
