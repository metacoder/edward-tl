package de.metacoder.edwardthreadlocal.analysis

import scala.language.postfixOps

import de.metacoder.edwardthreadlocal.analysis.datamodel.CallData

import scala.language.postfixOps

object CallDataProcessor {
  def addToCurrentSeries(cd:CallData)(implicit setup:AnalysisSetup):Unit =
    currentSeriesOpt filter (_ recordingEnabled) foreach {series ⇒
      storeCurrentSeries(series + currentValueInfoFor(cd threadLocal) + cd)
    }

  private def currentValueInfoFor(tl:ThreadLocal[_])(implicit setup:AnalysisSetup) =
    CallData.forCurrentValueInfo(tl, offTheRecord(tl.get().asInstanceOf[AnyRef]))

  def startRecordingSeries():Unit = currentSeriesOpt match {
    case None ⇒ storeCurrentSeries(emptySeries)
    case Some(disabled) if !disabled.recordingEnabled ⇒ storeCurrentSeries(disabled withRecordingEnabled true)
    case anythingElse ⇒ ()
  }

  def finishCurrentRecordingSeries()(implicit setup:AnalysisSetup):Seq[CallData] = {
    val result = currentSeriesOpt map (series ⇒ withAllCurrentValuesBehind(series recordedData)) getOrElse Seq()
    removeCurrentSeries()
    result
  }
  private def withAllCurrentValuesBehind(seriesData:Seq[CallData])(implicit setup:AnalysisSetup):Seq[CallData] =
    seriesData ++ ((seriesData map (_ threadLocal) distinct) map {tl ⇒ currentValueInfoFor(tl)})

  private case class Series(recordingEnabled:Boolean, recordedData:Seq[CallData]) {
    def +(cd:CallData):Series = copy(recordedData = recordedData :+ cd)
    def ++(cds:Iterable[CallData]):Series = cds.foldLeft(this)(_ + _)
    def withRecordingEnabled(enabled:Boolean):Series = copy(recordingEnabled = enabled)
  }

  private val emptySeries = Series(recordingEnabled = true, recordedData = Seq())

  private var currentSeries:Map[Thread, Series] = Map()
  private val currentSeriesMutex = new AnyRef
  private def inMutex[T](f: ⇒ T):T = currentSeriesMutex synchronized f

  private def currentSeriesOpt:Option[Series] =
    inMutex(currentSeries.get(Thread currentThread))
  private def storeCurrentSeries(series:Series) =
    inMutex(currentSeries = currentSeries + (Thread.currentThread → series))
  private def removeCurrentSeries() =
    inMutex(currentSeries = currentSeries - Thread.currentThread)

  private def offTheRecord[T](doWhat: ⇒ T):T = {
    val seriesBefore = currentSeriesOpt
    seriesBefore foreach {f ⇒ storeCurrentSeries(f withRecordingEnabled false)}
    val result:T = doWhat
    seriesBefore foreach storeCurrentSeries
    result
  }
}
