package de.metacoder.edwardthreadlocal.util.logging;

import java.util.function.Supplier;

public interface MicroLogger {
  void error(Throwable throwable);

  void fine(String message);

  void info(String message);

  <T> T infoAround(String blockName, Supplier<T> body);

  void infoBlock(String blockName, Runnable body);

  void setErrorActive(boolean active);

  void setFineActive(boolean active);

  void setInfoActive(boolean active);

  void setWarnActive(boolean active);

  void warn(String message);

  void warn(String message, Throwable throwable);
}
