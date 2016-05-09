package de.metacoder.edwardthreadlocal;

import de.metacoder.edwardthreadlocal.bytecodemanipulation.TestMainClassModification;
import de.metacoder.edwardthreadlocal.bytecodemanipulation.ThreadLocalClassModification;
import de.metacoder.edwardthreadlocal.util.logging.Log;

import java.lang.instrument.Instrumentation;

public class AgentEdward {

  public AgentEdward(String agentArgs, Instrumentation inst) throws Exception {
    Log.DEFAULT_LOGGER.setFineActive(false);

    EventBridgeHolder.INSTANCE = new EventBridgeImpl();

    AsciiArt.show();
    ThreadLocalClassModification.apply(inst);
    TestMainClassModification.apply(inst);
  }

}
