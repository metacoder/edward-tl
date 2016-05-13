package de.metacoder.edwardthreadlocal

import de.metacoder.edwardthreadlocal.analysis.datamodel.CallData
import de.metacoder.edwardthreadlocal.analysis.{AnalysisSetup, CallDataSink}

class EventBridgeImpl extends EventBridge {
  private implicit val setup:AnalysisSetup = AnalysisSetup.default

  override def activateTracingForThread() = CallDataSink startRecordingSeries()

  override def deactivateTracingForThread() = CallDataSink endRecordingSeries()

  override def trackRemove(affectedThreadLocal:ThreadLocal[_]) =
    CallDataSink accept (CallData forCallToRemove affectedThreadLocal)
  
  override def trackSet(affectedThreadLocal:ThreadLocal[_], valueToSet:Any) =
    CallDataSink accept (CallData forCallToSet(affectedThreadLocal, valueToSet.asInstanceOf[AnyRef]))
}
