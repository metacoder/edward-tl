package de.metacoder.edwardthreadlocal.analysis

import java.util.concurrent.atomic.AtomicLong

import de.metacoder.edwardthreadlocal.analysis.datamodel.CallData.{CallToRemove, CallToSet, CurrentValueInfo}
import de.metacoder.edwardthreadlocal.analysis.datamodel.{CallData, ValueInstanceID}

import scala.annotation.tailrec

object CallDataSeriesSink {
  def accept(series:Seq[CallData])(implicit setup:AnalysisSetup):Unit = {
    val serNum = newSeriesNumber()
    postProcessedSeriesPerThreadLocal(series) foreach {case (tl,s)⇒
      if(looksFaulty(s)) FaultyCallDataSeriesSink.accept(s, tl, serNum)
    }
  }

  private def postProcessedSeriesPerThreadLocal(series:Seq[CallData]):Map[ThreadLocal[_],Seq[CallData]] =
    series groupBy {_ threadLocal} mapValues postProcessedSeriesForOneThreadLocal filterNot {_._2 isEmpty}
  private def postProcessedSeriesForOneThreadLocal(series:Seq[CallData]):Seq[CallData] = {
    def isInterestingConsecutivePair(before:CallData, after:CallData, isLastPair:Boolean) =
      if(before.threadLocal != after.threadLocal) true
      else (before,after) match {
        case (_:CallToRemove, CurrentValueInfo(_,nullInstanceID)) if nullInstanceID.refersToNull && !isLastPair ⇒ false
        case (CurrentValueInfo(_,same1), CurrentValueInfo(_,same2)) if same1==same2 ⇒ false
        case (CallToSet(_,same1,_), CurrentValueInfo(_,same2)) if same1==same2 && !isLastPair ⇒ false
        case (CallToSet(_,same1,_), CallToSet(_,same2,_)) if same1==same2 ⇒ false
        case _ ⇒ true
      }
    def withoutBoringSubSequences(series:Seq[CallData]):Seq[CallData] = series match {
      case Seq() | Seq(_) ⇒ series
      case Seq(fst,snd,rst@_*) ⇒
        if(!isInterestingConsecutivePair(fst,snd,rst isEmpty)) withoutBoringSubSequences(fst+:rst)
        else fst+:withoutBoringSubSequences(snd+:rst)
    }
    withoutBoringSubSequences(series)
  }

  private def looksFaulty(series:Seq[CallData]):Boolean = {
    @tailrec def recurse(s:Seq[CallData],initialValue:Option[ValueInstanceID],lastSeenValue:Option[ValueInstanceID]):Boolean = s match {
      case Seq() ⇒ initialValue!=lastSeenValue
      case Seq(CurrentValueInfo(_,cv),rst@_*) if initialValue isEmpty ⇒ recurse(s, Some(cv), lastSeenValue)
      case Seq(CurrentValueInfo(_,cv),rst@_*) ⇒ recurse(rst, initialValue, Some(cv))
      case Seq(CallToSet(_,cv,_),rst@_*) if initialValue isEmpty ⇒ recurse(s, Some(cv), lastSeenValue)
      case Seq(CallToSet(_,cv,_),rst@_*) ⇒ recurse(rst, initialValue, Some(cv))
      case Seq(_:CallToRemove,rst@_*) ⇒ recurse(rst, initialValue, None)
      case Seq(_,rst@_*) ⇒ recurse(rst, initialValue, lastSeenValue)
    }
    recurse(series, None, None)
  }

  private val newSeriesNumber:()⇒Long = {val nextNumber=new AtomicLong; ()⇒nextNumber getAndIncrement()}
}
