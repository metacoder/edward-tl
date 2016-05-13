package de.metacoder.edwardthreadlocal.analysis.datamodel

import de.metacoder.edwardthreadlocal.analysis.AnalysisSetup

sealed trait CallData {
  def threadLocal:ThreadLocal[_]
}
object CallData {
  def forCallToRemove(tl:ThreadLocal[_]):CallToRemove =
    CallToRemove(tl, StackTrace.current())
  def forCallToSet(tl:ThreadLocal[_], newValue:AnyRef)(implicit setup:AnalysisSetup):CallToSet =
    CallToSet(tl, setup idOfValue newValue, StackTrace.current())
  def forCurrentValueInfo(tl:ThreadLocal[_], currentValue:AnyRef)(implicit setup:AnalysisSetup):CurrentValueInfo =
    CurrentValueInfo(tl, setup idOfValue currentValue)

  sealed case class CallToRemove(threadLocal:ThreadLocal[_], stackTrace:StackTrace) extends CallData
  sealed case class CallToSet(threadLocal:ThreadLocal[_], valueID:ValueInstanceID, stackTrace:StackTrace) extends CallData
  sealed case class CurrentValueInfo(threadLocal:ThreadLocal[_], currentValueID:ValueInstanceID) extends CallData
}
