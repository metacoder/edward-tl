package de.metacoder.edwardthreadlocal.util;

import de.metacoder.edwardthreadlocal.TraceReceiver;

import java.io.*;
import java.net.URISyntaxException;
import java.util.jar.JarFile;

import static de.metacoder.edwardthreadlocal.util.Preconditions.notNull;
import static de.metacoder.edwardthreadlocal.util.logging.Log.info;
import static de.metacoder.edwardthreadlocal.util.logging.Log.infoAround;

public final class IOUtils {
  private IOUtils() {
    throw new UnsupportedOperationException();
  }

  public static byte[] loadResourceBytes(String path) {
    return infoAround("loadResourcesBytes(\"" + path + "\")", () -> {
      try {
        byte[] result = readFullyAndClose(notNull(String.class.getResourceAsStream(path), "resource at path \"" + path + "\""));
        info(result.length + " bytes loaded");
        return result;
      } catch(IOException cause) {
        throw new RuntimeException(cause);
      }
    });
  }

  private static byte[] readFullyAndClose(InputStream is) throws IOException {
    byte[] result = transferFully(is, new ByteArrayOutputStream()).toByteArray();
    is.close();
    return result;
  }

  private static <O extends OutputStream> O transferFully(InputStream in, O out) throws IOException {
    byte[] buffer = new byte[1024];
    for(int bytesRead; (bytesRead = in.read(buffer)) != -1; ) out.write(buffer, 0, bytesRead);
    return out;
  }

  public static JarFile getAgentJarFile() throws IOException, URISyntaxException {
    return new JarFile(new File(TraceReceiver.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
  }
}
