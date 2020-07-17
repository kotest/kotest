package io.kotest.core

import io.kotest.core.spec.Spec
import io.kotest.core.tags.Expression
import io.kotest.core.tags.Parser
import io.kotest.core.tags.asString
import io.kotest.core.tags.expression
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

enum class IncludeExclude {
   Included, Excluded, None
}

/**
 * Runtime status of tags.
 */
data class Tags(val expression: Expression?) {

   companion object {

      val Empty = Tags(null)

      operator fun invoke(expression: String) = Tags(Parser.from(expression).expression())

      fun exclude(tag: Tag): Tags = Tags("!" + tag.name)
      fun include(tag: Tag): Tags = Tags(tag.name)

      /**
       * Backwards compatible version of tags. Note, this way you cannot do ANDs
       */
      operator fun invoke(included: Set<Tag>, excluded: Set<Tag>): Tags = when {
         included.isEmpty() && excluded.isEmpty() -> Empty
         included.isEmpty() -> Tags(excluded.joinToString(" | ") { "!" + it.name })
         excluded.isEmpty() -> Tags(included.joinToString(" | ") { it.name })
         else -> Tags(
            included.joinToString(" | ", "(", ")") { it.name } + " & " +
               excluded.joinToString(" | ", "(", ")") { "!" + it.name }
         )
      }
   }

   fun isActive(tag: Tag): Boolean = isActive(setOf(tag))

   fun combine(other: Tags): Tags = when {
      this.expression == null -> other
      other.expression == null -> this
      else -> Tags(this.expression.asString() + " & " + other.expression.asString())
   }

   /**
    * Returns true if the given Spec class could contain an active test based on further tags.
    * Returns false if the spec should be explictly excluded
    */
   fun isPotentiallyActive(kclass: KClass<out Spec>): Boolean {
      // nothing is excluded if the expression is null
      if (expression == null) return true

      // if the class is not tagged then it is not excluded
      val tags = kclass.tags()
      if (tags.isEmpty()) return true

      return isPotentiallyActive(tags.toSet(), expression) ?: true
   }

   private fun isPotentiallyActive(tags: Set<Tag>, expression: Expression): Boolean? {
      return when (expression) {
         is Expression.Or ->
            isPotentiallyActive(tags, expression.left) ?: true || isPotentiallyActive(tags, expression.right) ?: true
         is Expression.And ->
            isPotentiallyActive(tags, expression.left) ?: true && isPotentiallyActive(tags, expression.right) ?: true
         is Expression.Not -> isPotentiallyActive(tags, expression.expr)?.not()
         is Expression.Identifier -> if (tags.map { it.name }.contains(expression.ident)) true else null
      }
   }

   /**
    * Returns true if the the given set of tags should be considered active based
    * on the current tag expression.
    */
   fun isActive(tags: Set<Tag>): Boolean {
      // everything is always active when no tag expression is provided
      if (expression == null) return true
      return evaluate(tags, expression)
   }

   private fun evaluate(tags: Set<Tag>, expression: Expression): Boolean {
      return when (expression) {
         is Expression.Or -> evaluate(tags, expression.left) || evaluate(tags, expression.right)
         is Expression.And -> evaluate(tags, expression.left) && evaluate(tags, expression.right)
         is Expression.Not -> !evaluate(tags, expression.expr)
         is Expression.Identifier -> tags.map { it.name }.contains(expression.ident)
      }
   }

   fun include(tag: Tag): Tags = combine(Tags(tag.name))
   fun exclude(tag: Tag): Tags = combine(Tags("!" + tag.name))
}

private fun IncludeExclude.invert(): IncludeExclude = when (this) {
   IncludeExclude.Included -> IncludeExclude.Excluded
   IncludeExclude.Excluded -> IncludeExclude.Included
   IncludeExclude.None -> this
}

fun KClass<*>.tags(): Set<Tag> {
   val annotation = annotation<io.kotest.core.annotation.Tags>() ?: return emptySet<Tag>()
   return annotation.values.map { StringTag(it) }.toSet()
}
