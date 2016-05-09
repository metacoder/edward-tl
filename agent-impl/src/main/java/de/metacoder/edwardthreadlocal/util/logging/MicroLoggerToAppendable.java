package de.metacoder.edwardthreadlocal.util.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static de.metacoder.edwardthreadlocal.util.Preconditions.notNull;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedSet;

class MicroLoggerToAppendable implements MicroLogger {
  private final ConcurrentLinkedDeque<String> context = new ConcurrentLinkedDeque<>();
  private final AtomicBoolean errorActive = new AtomicBoolean(true);
  private final AtomicBoolean fineActive = new AtomicBoolean(true);
  private final AtomicBoolean infoActive = new AtomicBoolean(true);
  private final Appendable out;
  private final Set<RuntimeException> rethrownExceptions = synchronizedSet(newSetFromMap(new WeakHashMap<>()));
  private final AtomicBoolean warnActive = new AtomicBoolean(true);

  MicroLoggerToAppendable(Appendable out) {
    this.out = notNull(out, "out");
  }

  @Override
  public void error(Throwable throwable) {
    if(errorActive.get()) appendNewMessageHeader("ERROR").append(throwable, "ERROR").append("\n");
  }

  protected MicroLoggerToAppendable appendNewMessageHeader(String logLevel) {
    append("[").append(logLevel).append("] ");
    for(String contextElement : context) append("<").append(contextElement).append("> ");
    return this;
  }

  protected MicroLoggerToAppendable append(Throwable throwable, String logLevel) {
    if(throwable != null) throwable.printStackTrace(new PrintWriter(getWriterForLevel(logLevel)));
    return this;
  }

  private Writer getWriterForLevel(String logLevel) {
    return new Writer() {
      private boolean atLineStart = false;

      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        for(int charCount = 0; charCount < len; ++charCount) {
          if(atLineStart) {
            appendNewMessageHeader(logLevel);
            atLineStart = false;
          }
          char ch = cbuf[off + charCount];
          out.append(ch);
          if(ch == '\n' || ch == '\r') atLineStart = true;
        }
      }

      @Override
      public void flush() throws IOException {
      }

      @Override
      public void close() throws IOException {
      }
    };
  }

  protected MicroLoggerToAppendable append(String str) {
    try {
      out.append(str);
    } catch(IOException cause) {
      throw new RuntimeException(cause);
    }
    return this;
  }

  @Override
  public void fine(String message) {
    if(fineActive.get()) log("FINE", message);
  }

  @Override
  public void info(String message) {
    if(infoActive.get()) log("INFO", message);
  }

  protected void log(String logLevel, String message) {
    appendNewMessageHeader(logLevel).append(message).append("\n");
  }

  @Override
  public <T> T infoAround(String blockName, Supplier<T> body) {
    final long startMillis = System.currentTimeMillis();
    try {
      context.addLast(blockName);
      info("BEGIN");
      try {
        return body.get();
      } catch(RuntimeException cause) {
        if(!rethrownExceptions.contains(cause)) {
          rethrownExceptions.add(cause);
          warn("Logger block threw exception. Logging and re-throwing.", cause);
        }
        throw cause;
      }
    } finally {
      info("END (" + (System.currentTimeMillis() - startMillis) + "ms)");
      context.removeLast();
    }
  }

  @Override
  public void infoBlock(String blockName, Runnable body) {
    infoAround(blockName, () -> {
      body.run();
      return null;
    });
  }

  @Override
  public void setErrorActive(boolean active) {
    errorActive.set(active);
  }

  @Override
  public void setFineActive(boolean active) {
    fineActive.set(active);
  }

  @Override
  public void setInfoActive(boolean active) {
    infoActive.set(active);
  }

  @Override
  public void setWarnActive(boolean active) {
    warnActive.set(active);
  }

  @Override
  public void warn(String message) {
    if(warnActive.get()) log("WARN", message);
  }

  @Override
  public void warn(String message, Throwable throwable) {
    if(warnActive.get()) {
      warn(message);
      append(throwable, "WARN");
    }
  }
}
