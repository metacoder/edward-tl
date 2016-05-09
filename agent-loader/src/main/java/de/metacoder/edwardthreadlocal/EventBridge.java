package de.metacoder.edwardthreadlocal;

public interface EventBridge {

  void trackSet(ThreadLocal<?> affectedThreadLocal, Object valueToSet);
  void trackRemove(ThreadLocal<?> affectedThreadLocal);

  void deactivateTracingForThread();
  void activateTracingForThread();

}
