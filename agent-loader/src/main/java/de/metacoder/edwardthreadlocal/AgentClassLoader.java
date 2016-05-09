package de.metacoder.edwardthreadlocal;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by becker on 5/9/16.
 */
public class AgentClassLoader extends URLClassLoader {

  private final ClassLoader parent;

  public AgentClassLoader(URL[] urls, ClassLoader parent) throws MalformedURLException {
    super(urls);
    assert parent != null;
    this.parent = parent;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
      Class<?> c = findLoadedClass(name);
      if (c == null) {
        try {
          c = findClass(name);
        } catch (ClassNotFoundException e) {}

        if (c == null) {
          c = parent.loadClass(name);
        }
      }

      return c;
    }
  }
}
