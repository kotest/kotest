package io.kotest.core

/**
 * Contains an expression such as '(linux | mac) & mysql' which determines which tags are active
 * or inactive during test execution.
 */
data class Tags(val expression: String?) {

   companion object {

      val Empty = Tags(null)

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

   fun combine(other: Tags): Tags = when {
      this.expression == null -> other
      other.expression == null -> this
      else -> Tags(this.expression + " & " + other.expression)
   }

   fun include(tag: Tag): Tags = combine(Tags(tag.name))
   fun exclude(tag: Tag): Tags = combine(Tags("!" + tag.name))
}
