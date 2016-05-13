package de.metacoder.edwardthreadlocal.analysis.datamodel

import de.metacoder.edwardthreadlocal

// other than a StackTraceElement array, this can be compared with equals, and returns a meaningful toString
case class StackTrace(elements:Seq[StackTraceElement])
object StackTrace {
  private val trivialStackLocalMethodNames = Set("set", "remove")
  def current():StackTrace = {
    def removeInitialStackTraceCall(st:Seq[StackTraceElement]):Seq[StackTraceElement] = st match {
      case Seq(getStackTraceCall, rst@_*) if getStackTraceCall.getClassName == classOf[Thread].getName ⇒ rst
      case _ ⇒ st
    }
    def removeInitialEdwardThreadLocalCalls(st:Seq[StackTraceElement]):Seq[StackTraceElement] = st match {
      case Seq(edwardThreadLocalCall, rst@_*) if edwardThreadLocalCall.getClassName.startsWith(edwardthreadlocal.packageName) ⇒
        removeInitialEdwardThreadLocalCalls(rst)
      case _ ⇒ st
    }
    def removeInitialTrivialThreadLocalCalls(st:Seq[StackTraceElement]):Seq[StackTraceElement] = st match {
      case Seq(tlCall, rst@_*) if tlCall.getClassName == classOf[ThreadLocal[_]].getName && trivialStackLocalMethodNames(tlCall getMethodName) ⇒
        removeInitialTrivialThreadLocalCalls(rst)
      case _ ⇒ st
    }
    val removeReduntantIntials:Seq[StackTraceElement] ⇒ Seq[StackTraceElement] =
      removeInitialStackTraceCall _ andThen removeInitialEdwardThreadLocalCalls andThen removeInitialTrivialThreadLocalCalls
    StackTrace(removeReduntantIntials(Thread.currentThread().getStackTrace toSeq))
  }
}
