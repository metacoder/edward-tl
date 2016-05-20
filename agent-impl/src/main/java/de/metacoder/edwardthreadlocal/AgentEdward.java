package de.metacoder.edwardthreadlocal;

import de.metacoder.edwardthreadlocal.bytecodemanipulation.TestMainClassModification;
import de.metacoder.edwardthreadlocal.bytecodemanipulation.ThreadLocalClassModification;
import de.metacoder.edwardthreadlocal.util.logging.Log;

import java.lang.instrument.Instrumentation;

public class AgentEdward {



  public AgentEdward(String agentArgs, Instrumentation inst) throws Exception {
    Log.DEFAULT_LOGGER.setFineActive(false);

    final EventBridge internalBridge = new EventBridgeImpl();


    EventBridgeHolder.INSTANCE = new EventBridge() {
      @Override
      public void trackSet(ThreadLocal<?> affectedThreadLocal, Object valueToSet) {
        if(!CallStackUtil.threadLocalDirectlyCalledByJavaLangClass(Thread.currentThread().getStackTrace()))
        internalBridge.trackSet(affectedThreadLocal, valueToSet);
      }

      @Override
      public void trackRemove(ThreadLocal<?> affectedThreadLocal) {
        if(!CallStackUtil.threadLocalDirectlyCalledByJavaLangClass(Thread.currentThread().getStackTrace()))
        internalBridge.trackRemove(affectedThreadLocal);
      }

      @Override
      public void deactivateTracingForThread() {
        internalBridge.deactivateTracingForThread();
      }

      @Override
      public void activateTracingForThread() {
        internalBridge.activateTracingForThread();
      }
    };

    AsciiArt.show();
    ThreadLocalClassModification.apply(inst);
    TestMainClassModification.apply(inst);
  }

}
