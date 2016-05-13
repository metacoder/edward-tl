package de.metacoder.edwardthreadlocal

import de.metacoder.edwardthreadlocal.analysis.AnalysisSetup
import de.metacoder.edwardthreadlocal.analysis.datamodel.CallData

class EventBridgeImpl extends EventBridge {
  private implicit val setup:AnalysisSetup = AnalysisSetup.default

  override def activateTracingForThread() = DataFlowController.startRecordingSeries()

  override def deactivateTracingForThread() = DataFlowController.endRecordingSeries()

  override def trackRemove(affectedThreadLocal:ThreadLocal[_]) =
    DataFlowController addToCurrentSeries (CallData forCallToRemove affectedThreadLocal)

  override def trackSet(affectedThreadLocal:ThreadLocal[_], valueToSet:Any) =
    DataFlowController addToCurrentSeries (CallData forCallToSet(affectedThreadLocal, valueToSet.asInstanceOf[AnyRef]))
}
