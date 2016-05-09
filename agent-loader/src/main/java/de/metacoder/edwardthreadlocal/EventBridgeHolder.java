package de.metacoder.edwardthreadlocal;

import java.lang.reflect.Proxy;


public class EventBridgeHolder {

 /*
  This class must be loaded by the bootstrap class loader
 */
  static {
    assert EventBridgeHolder.class.getClassLoader() == null;
  }

  /*
    this is the eventbridge implemented by the agent-impl
  */
  public static EventBridge INSTANCE = null;

  //public static EventBridge INSTANCE = (EventBridge) Proxy.newProxyInstance(null, new Class[]{EventBridge.class}, (proxy, method, args) -> method.invoke(INSTANCE, args));

}
