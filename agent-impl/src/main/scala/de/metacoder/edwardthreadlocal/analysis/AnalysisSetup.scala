package de.metacoder.edwardthreadlocal.analysis

import de.metacoder.edwardthreadlocal.analysis.datamodel.ValueInstanceID
import de.metacoder.edwardthreadlocal.util.logging.{Log, MicroLogger}

import scala.annotation.implicitNotFound

@implicitNotFound(msg = "Make sure the AnalysisSetup instance is in the implicit context. It contains behavior that might be made configurable. In case of doubt, use `AnalysisSetup.default`.")
trait AnalysisSetup {
  def idOfValue(v:AnyRef):ValueInstanceID
  def log:MicroLogger
}
object AnalysisSetup {
  val default:AnalysisSetup = new AnalysisSetup {
    def idOfValue(v:AnyRef) = ValueInstanceID.ByClassAndSystemIndentityHashCode of v
    def log = Log.DEFAULT_LOGGER
  }
}
