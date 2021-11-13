package io.kotest.core

/**
 * Contains an expression such as '(linux | mac) & mysql' which determines which tags are active
 * or inactive during test execution.
 */
data class TagExpression(val expression: String) {

   companion object {

      val Empty = TagExpression("")

      fun exclude(tag: Tag): TagExpression = TagExpression("!" + tag.name)
      fun include(tag: Tag): TagExpression = TagExpression(tag.name)

      /**
       * Backwards compatible version of tags. Note, this way you cannot do ANDs
       */
      operator fun invoke(included: Set<Tag>, excluded: Set<Tag>): TagExpression = when {
         included.isEmpty() && excluded.isEmpty() -> Empty
         included.isEmpty() -> TagExpression(excluded.joinToString(" | ") { "!" + it.name })
         excluded.isEmpty() -> TagExpression(included.joinToString(" | ") { it.name })
         else -> TagExpression(
            included.joinToString(" | ", "(", ")") { it.name } + " & " +
               excluded.joinToString(" | ", "(", ")") { "!" + it.name }
         )
      }
   }

   fun combine(other: TagExpression): TagExpression = when {
      this.expression == "" -> other
      other.expression == "" -> this
      else -> TagExpression(this.expression + " & " + other.expression)
   }

   /**
    * Returns a new [TagExpression] which is the result of this expression and including the given tag.
    */
   fun include(tag: Tag): TagExpression = combine(TagExpression(tag.name))

   fun include(tag: String): TagExpression = combine(TagExpression(tag))

   /**
    * Returns a new [TagExpression] which is the result of this expression and excluding the given tag.
    */
   fun exclude(tag: Tag): TagExpression = combine(TagExpression("!" + tag.name))

   fun exclude(tag: String): TagExpression = combine(TagExpression("!$tag"))
}
