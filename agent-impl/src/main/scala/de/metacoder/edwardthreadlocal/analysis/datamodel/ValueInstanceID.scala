package de.metacoder.edwardthreadlocal.analysis.datamodel

import de.metacoder.edwardthreadlocal.analysis.AnalysisSetup

sealed trait ValueInstanceID {
  def refersToNull:Boolean
}
object ValueInstanceID {
  def of(value:AnyRef)(implicit setup:AnalysisSetup):ValueInstanceID = setup idOfValue value

  private[analysis] sealed case class ByClassAndSystemIndentityHashCode(clazz:Class[_], systemIdentityHashCode:Int) extends ValueInstanceID {
    def refersToNull = clazz == null
    override def toString = if (refersToNull) "null" else s"${clazz getName}@$systemIdentityHashCode"
  }
  private[analysis] object ByClassAndSystemIndentityHashCode {
    def of(v:AnyRef) = v match {
      case null ⇒ ByClassAndSystemIndentityHashCode(null, 0)
      case notNull ⇒ ByClassAndSystemIndentityHashCode(v getClass, System identityHashCode v)
    }
  }
}
