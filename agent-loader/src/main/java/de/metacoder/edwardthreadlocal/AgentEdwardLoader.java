package de.metacoder.edwardthreadlocal;

import java.io.File;
import java.io.FileFilter;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class AgentEdwardLoader {

  public static void premain(String args, Instrumentation instrumentation) throws Exception {
    final String jarFileSuffix = ".jar";
    final String agentFqcn = "de.metacoder.edwardthreadlocal.AgentEdward";

    final URL agentEdwardLoaderJarLocation = AgentEdwardLoader.class.getProtectionDomain().getCodeSource().getLocation();
    final File agentDir = new File(agentEdwardLoaderJarLocation.toURI()).getParentFile();

    /*
      bootstrap classloader needs to now the EventBridgeHolder class
    */
    log("Adding loader jar to the bootstrap classloader");
    instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(new File(agentEdwardLoaderJarLocation.toURI())));

    log("Location of the agent edward loader jar is: " + agentEdwardLoaderJarLocation.getPath());
    log("Location of the agent directory is: " + agentDir.getAbsolutePath());

    try {

      final List<File> jarFiles = getJarFiles(agentDir, jarFileSuffix, new File(agentEdwardLoaderJarLocation.toURI()));

      final URL[] jarUrls = new URL[jarFiles.size()];
      for(int i = 0; i < jarFiles.size(); i++){
        jarUrls[i] = jarFiles.get(i).toURI().toURL();
      }

      final AgentClassLoader agentClassLoader = new AgentClassLoader(jarUrls, AgentEdwardLoader.class.getClassLoader());
      agentClassLoader.loadClass(agentFqcn).getConstructor(String.class, Instrumentation.class).newInstance(args, instrumentation);
    } catch (Exception e) {
      System.err.println("Fatal error occured while loading agent edward java agent!");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  private static void log(String text) {
    System.out.println("[" + System.currentTimeMillis() + "]: " + text);
  }

  private static List<File> getJarFiles(final File agentDir, final String jarFileSuffix, File excludedLoaderjar) {
    final List<File> jarFiles = new ArrayList<>();

    final FileFilter jarFileFilter = pathname -> pathname.isFile() && pathname.getName().endsWith(jarFileSuffix);

    for(File file : agentDir.listFiles(jarFileFilter)){
      if(!file.equals(excludedLoaderjar)) {
        jarFiles.add(file);
        log("Added file " + file.getAbsolutePath() + " to the classpath");
      } else {
        log("Not appending " + file.getAbsolutePath() + " to agent class loader because it's the excluded loader jar");
      }
    }

    return jarFiles;
  }

}