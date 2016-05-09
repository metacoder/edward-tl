package de.metacoder.edwardthreadlocal

/**
  * Created by becker on 5/9/16.
  */
class EventBridgeImpl extends EventBridge {

  override def trackSet(affectedThreadLocal: ThreadLocal[_], valueToSet: scala.Any): Unit = println("I am the bridge impl trackSet")
  override def activateTracingForThread(): Unit = println("I am the bridge impl activateTracingForThread")
  override def deactivateTracingForThread(): Unit = println("I am the bridge impl deactivateTracingForThread")
  override def trackRemove(affectedThreadLocal: ThreadLocal[_]): Unit = println("I am the bridge impl trackRemove")

}
