package de.metacoder.edwardthreadlocal

import de.metacoder.edwardthreadlocal.analysis.datamodel.CallData
import de.metacoder.edwardthreadlocal.analysis.{AnalysisSetup, CallDataProcessor, FindFaultySubSeries, ReportFaultyCallSeries}

object DataFlowController {
  def startRecordingSeries() =
    CallDataProcessor.startRecordingSeries()

  def addToCurrentSeries(callData:CallData)(implicit setup:AnalysisSetup) =
    CallDataProcessor addToCurrentSeries callData

  def endRecordingSeries()(implicit setup:AnalysisSetup) = {
    val recordedSeries = CallDataProcessor.finishCurrentRecordingSeries()
    val faultySubSeries = FindFaultySubSeries(recordedSeries)
    for ((threadLocal, subSeries) ← faultySubSeries)
      ReportFaultyCallSeries(subSeries, threadLocal)
  }
}
