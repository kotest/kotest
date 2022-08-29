package io.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Returns true if the given [Spec] class could contain an active test based on further tags.
 * Returns false if the spec has been explicitly excluded and should not be instantiated.
 */
fun Expression?.isPotentiallyActive(kclass: KClass<out Spec>, conf: ProjectConfiguration): Boolean {
   // nothing is excluded if the expression is null
   if (this == null) return true

   // if the class is not tagged then it is not excluded
   val tags = kclass.tags(conf.tagInheritance)
   if (tags.isEmpty()) return true

   return isPotentiallyActive(tags.toSet())
}

/**
 * Returns true if the given [Spec] class could contain an active test based on further tags.
 * Returns false if the spec has been explicitly excluded and should not be instantiated.
 */
internal fun Expression?.isPotentiallyActive(tags: Set<Tag>): Boolean {

   // nothing is excluded if the expression is null
   if (this == null) return true
   if (tags.isEmpty()) return true

   return when (this) {
      is Expression.Or -> left.isPotentiallyActive(tags) || right.isPotentiallyActive(tags)
      is Expression.And -> left.isPotentiallyActive(tags) && right.isPotentiallyActive(tags)
      is Expression.Not -> !expr.isPotentiallyActive(tags)
      is Expression.Identifier -> tags.map { it.name }.contains(ident)
   }
}

/**
 * Returns true if the the given tag should be considered active based
 * on the current tag expression.
 */
fun Expression?.isActive(tag: Tag): Boolean = isActive(setOf(tag))

/**
 * Returns true if the the given set of tags should be considered active based
 * on the current tag expression.
 */
fun Expression?.isActive(tags: Set<Tag>): Boolean {
   // everything is always active when no tag expression is provided
   if (this == null) return true
   return evaluate(tags, this)
}

private fun evaluate(tags: Set<Tag>, expression: Expression): Boolean {
   return when (expression) {
      is Expression.Or -> evaluate(tags, expression.left) || evaluate(tags, expression.right)
      is Expression.And -> evaluate(tags, expression.left) && evaluate(tags, expression.right)
      is Expression.Not -> !evaluate(tags, expression.expr)
      is Expression.Identifier -> tags.map { it.name }.contains(expression.ident)
   }
}
