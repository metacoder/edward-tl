package de.metacoder.edwardthreadlocal.analysis.datamodel

import de.metacoder.edwardthreadlocal.analysis.AnalysisSetup

sealed trait CallData {
  def threadLocal:ThreadLocal[_]
}
object CallData {
  def forCallToRemove[A](tl:ThreadLocal[A]):CallToRemove[A] =
    CallToRemove(tl, StackTrace.current())
  def forCallToSet[A](tl:ThreadLocal[A], newValue:AnyRef)(implicit setup:AnalysisSetup):CallToSet[A] =
    CallToSet(tl, setup idOfValue newValue, StackTrace.current())
  def forCurrentValueInfo[A](tl:ThreadLocal[A], currentValue:AnyRef)(implicit setup:AnalysisSetup):CurrentValueInfo[A] =
    CurrentValueInfo(tl, setup idOfValue currentValue)

  sealed case class CallToRemove[A](threadLocal:ThreadLocal[A], stackTrace:StackTrace) extends CallData
  sealed case class CallToSet[A](threadLocal:ThreadLocal[A], valueID:ValueInstanceID, stackTrace:StackTrace) extends CallData
  sealed case class CurrentValueInfo[A](threadLocal:ThreadLocal[A], currentValueID:ValueInstanceID) extends CallData
}
