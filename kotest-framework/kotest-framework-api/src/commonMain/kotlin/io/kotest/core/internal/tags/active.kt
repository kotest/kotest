package io.kotest.core.internal.tags

import io.kotest.core.Tag
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Returns true if the given Spec class could contain an active test based on further tags.
 * Returns false if the spec should be explictly excluded
 */
fun Expression?.isPotentiallyActive(kclass: KClass<out Spec>): Boolean {
   // nothing is excluded if the expression is null
   if (this == null) return true

   // if the class is not tagged then it is not excluded
   val tags = kclass.tags()
   if (tags.isEmpty()) return true

   return isPotentiallyActive(tags.toSet()) ?: true
}

internal fun Expression.isPotentiallyActive(tags: Set<Tag>): Boolean? {
   return when (this) {
      is Expression.Or -> left.isPotentiallyActive(tags) ?: true || right.isPotentiallyActive(tags) ?: true
      is Expression.And -> left.isPotentiallyActive(tags) ?: true && right.isPotentiallyActive(tags) ?: true
      is Expression.Not -> expr.isPotentiallyActive(tags)?.not()
      is Expression.Identifier -> if (tags.map { it.name }.contains(ident)) true else null
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
