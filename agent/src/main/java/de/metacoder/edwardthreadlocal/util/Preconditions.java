package de.metacoder.edwardthreadlocal.util;

public final class Preconditions {
  private Preconditions() {
    throw new UnsupportedOperationException();
  }

  public static <T> T notNull(T value, String name) {
    if(value == null) throw new RuntimeException(name + " is null");
    return value;
  }
}
