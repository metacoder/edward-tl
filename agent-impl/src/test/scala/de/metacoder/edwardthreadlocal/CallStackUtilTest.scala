package de.metacoder.edwardthreadlocal

import org.scalatest.{FlatSpec, ShouldMatchers}

/**
  * Created by becker on 5/19/16.
  */
class CallStackUtilTest extends FlatSpec with ShouldMatchers {

  behavior of "a CallStackUtil"

  it should "return false when the thread local calls are followed by a java.lang class" in {

    CallStackUtil.threadLocalDirectlyCalledByJavaLangClass(
      StackTrace (
        ("java.lang.Thread", "getStackTrace", 1552),
        ("de.metacoder.edwardthreadlocal.AgentEdward", "isFirstThreadLocalOnCallStack", 18),
        ("de.metacoder.edwardthreadlocal.AgentEdward", "access$000", 12),
        ("de.metacoder.edwardthreadlocal.AgentEdward$1", "trackSet", 38),
        ("java.lang.ThreadLocal", "set", -1),
        ("java.lang.StringCoding", "set", 70),
        ("java.lang.StringCoding", "encode", 342),
        ("java.lang.StringCoding", "encode", 387),
        ("java.lang.String", "getBytes", 958),
        ("org.tanukisoftware.wrapper.WrapperResources", "nativeGetLocalizedString", -2),
        ("org.tanukisoftware.wrapper.WrapperResources", "getString", 87),
        ("org.tanukisoftware.wrapper.WrapperManager", "run", 5458),
        ("java.lang.Thread", "run", 745)
      )
    ) should be(true)


  }

  def StackTrace(elems: (String, String, Int)*): Array[StackTraceElement] = elems.map {
    case(clazz, method, line) => new StackTraceElement(clazz, method, null, line)
  }.toArray

}
