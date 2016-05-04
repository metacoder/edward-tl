package de.metacoder.edwardthreadlocal;

import de.metacoder.edwardthreadlocal.bytecodemanipulation.TestMainClassModification;
import de.metacoder.edwardthreadlocal.bytecodemanipulation.ThreadLocalClassModification;
import de.metacoder.edwardthreadlocal.util.IOUtils;
import de.metacoder.edwardthreadlocal.util.logging.Log;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

import static de.metacoder.edwardthreadlocal.util.logging.Log.info;

public class AgentEdward {
  public static void premain(String agentArgs, Instrumentation inst) throws Exception {
    Log.DEFAULT_LOGGER.setFineActive(false);

    AsciiArt.show();
    ThreadLocalClassModification.apply(inst);
    TestMainClassModification.apply(inst);
    attachAgentJarToBoostrapClassLoaderSearch(inst);
  }

  private static void attachAgentJarToBoostrapClassLoaderSearch(Instrumentation inst) throws IOException, URISyntaxException {
    final JarFile agentJar = IOUtils.getAgentJarFile();
    info("Attaching agent jar \"" + agentJar.getName() + "\" to bootstrap class loader search");
    inst.appendToBootstrapClassLoaderSearch(agentJar);
  }
}
