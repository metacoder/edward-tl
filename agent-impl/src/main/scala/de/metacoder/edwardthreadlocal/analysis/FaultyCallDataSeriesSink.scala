package de.metacoder.edwardthreadlocal.analysis

import de.metacoder.edwardthreadlocal.analysis.datamodel.CallData.{CallToRemove, CallToSet, CurrentValueInfo}
import de.metacoder.edwardthreadlocal.analysis.datamodel.{CallData, StackTrace}

object FaultyCallDataSeriesSink {
  def accept(series:Seq[CallData], threadLocal:ThreadLocal[_], seriesNumber:Long)(implicit setup:AnalysisSetup) {
    import setup.log
    log.warn(s"Possibly faulty series recognized, for $threadLocal, in series no. $seriesNumber:")
    var instructionCounter = 0
    def warn(msg:String) = log warn s"#$seriesNumber/$threadLocal[$instructionCounter] $msg"
    def warnStackTrace(st:StackTrace) = st.elements foreach {element ⇒ warn(s"  at $element")}
    series foreach {
      case CallToRemove(_, stackTrace) ⇒
        warn("ThreadLocal.remove()")
        warnStackTrace(stackTrace)
        instructionCounter += 1
      case CallToSet(_, value, stackTrace) ⇒
        warn(s"ThreadLocal.set($value)")
        warnStackTrace(stackTrace)
        instructionCounter += 1
      case CurrentValueInfo(_, value) ⇒
        warn(s"Value is now: $value")
        instructionCounter += 1
    }
  }
}
