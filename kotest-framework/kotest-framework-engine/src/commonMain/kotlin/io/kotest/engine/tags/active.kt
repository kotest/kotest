package io.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.spec.Spec
import io.kotest.engine.config.ProjectConfigResolver
import kotlin.reflect.KClass

/**
 * Returns true if the given [Spec] class could contain an active test based on further tags.
 * Returns false if the spec has been explicitly excluded and should not be instantiated.
 */
fun Expression?.isPotentiallyActive(kclass: KClass<out Spec>, projectConfigResolver: ProjectConfigResolver): TagExpressionResult {
   // nothing is excluded if the expression is null
   if (this == null) return TagExpressionResult.Include

   // if the class is not tagged then it is not excluded
   val tags = kclass.tags(projectConfigResolver.tagInheritance())
   if (tags.isEmpty()) return TagExpressionResult.Inconclusive

   return isPotentiallyActive(tags.toSet())
}

/**
 * Returns true if the given [Spec] class could contain an active test based on further tags.
 * Returns false if the spec has been explicitly excluded and should not be instantiated.
 */
internal fun Expression?.isPotentiallyActive(tags: Set<Tag>): TagExpressionResult {

   // nothing is excluded if the expression is null
   if (this == null) return TagExpressionResult.Include
   if (tags.isEmpty()) return TagExpressionResult.Inconclusive

   return when (this) {
      is Expression.Or -> left.isPotentiallyActive(tags) or right.isPotentiallyActive(tags)
      is Expression.And -> left.isPotentiallyActive(tags) and right.isPotentiallyActive(tags)
      is Expression.Not -> !expr.isPotentiallyActive(tags)
      is Expression.Identifier -> {
         if (tags.map { it.name }.contains(this.ident)) TagExpressionResult.Include
         else TagExpressionResult.Inconclusive
      }
   }
}

/**
 * Returns true if the given tag should be considered active based
 * on the current tag expression.
 */
fun Expression?.isActive(tag: Tag): Boolean = isActive(setOf(tag))

/**
 * Returns true if the given set of tags should be considered active based
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

enum class TagExpressionResult {
   /**
    * The tag expression is satisfied by the given tags.
    */
   Include,

   /**
    * The tag expression is explicitly denied by the given tags.
    */
   Exclude,

   /**
    * The tag expression never results in an include or exclude, so the tags are potentially active.
    */
   Inconclusive;

   operator fun not(): TagExpressionResult = when (this) {
      Include -> Exclude
      Exclude -> Include
      Inconclusive -> Inconclusive
   }

   infix fun and(other: TagExpressionResult): TagExpressionResult = when (this) {
      Include -> other
      Exclude -> Exclude
      Inconclusive -> when (other) {
         Include -> Inconclusive
         Exclude -> Exclude
         Inconclusive -> Inconclusive
      }
   }

   infix fun or(other: TagExpressionResult): TagExpressionResult = when (this) {
      Include -> Include
      Exclude -> if (other == Include) Include else Exclude
      Inconclusive -> when (other) {
         Include -> Include
         Exclude -> Exclude
         Inconclusive -> Inconclusive
      }
   }
}

