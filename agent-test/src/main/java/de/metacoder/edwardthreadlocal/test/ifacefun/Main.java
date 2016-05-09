package de.metacoder.edwardthreadlocal.test.ifacefun;

import java.lang.reflect.Proxy;

/**
 * Created by becker on 5/9/16.
 */
public class Main {

  public static void main(String... arg){

    TestInterface ti = (TestInterface) Proxy.newProxyInstance(Main.class.getClassLoader(), new Class[]{TestInterface.class}, (proxy, method, args) -> {
      System.out.println("Invoked method " + method.getName() + " with param " + args[0]);
      return null;
    });

    ti.greet("Felix");
    ti.hate("Felix");
  }
}
