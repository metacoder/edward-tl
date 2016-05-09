package de.metacoder.edwardthreadlocal.util.logging;

import java.util.function.Supplier;

public final class Log {
  private Log() {
    throw new UnsupportedOperationException();
  }

  public static final MicroLogger DEFAULT_LOGGER = new MicroLoggerToAppendable(System.out);

  public static void error(Throwable cause) {
    DEFAULT_LOGGER.error(cause);
  }

  public static void fine(String message) {
    DEFAULT_LOGGER.fine(message);
  }

  public static void info(String message) {
    DEFAULT_LOGGER.info(message);
  }

  public static <T> T infoAround(String blockName, Supplier<T> body) {
    return DEFAULT_LOGGER.infoAround(blockName, body);
  }

  public static void infoBlock(String blockName, Runnable body) {
    DEFAULT_LOGGER.infoBlock(blockName, body);
  }

  public static void warn(String message) {
    DEFAULT_LOGGER.warn(message);
  }
}
