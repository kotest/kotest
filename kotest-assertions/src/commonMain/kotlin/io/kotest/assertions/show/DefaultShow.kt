package io.kotest.assertions.show

/**
 * A default implementation of [Show] that handles arrays, collections and primitives as well as nullable values.
 */
object DefaultShow : Show<Any?> {
   override fun show(a: Any?): Printed = when (a) {
      null -> "<null>".printed()
      is Boolean -> "$a".printed()
      is Float -> "${a}f".printed()
      is Long -> "${a}L".printed()
      is Char -> "'$a'".printed()
      is Int -> a.toString().printed()
      is Short -> a.toString().printed()
      is Byte -> a.toString().printed()
      is String -> "\"$a\"".printed()
      else -> a.toString().printed()
   }
}

private const val MaxCollectionSnippetSize = 20

/**
 * Returns the values in a Collection, up to a max size
 */
private fun Collection<*>.getCollectionSnippet(): Printed {
   val remainingItems = size - MaxCollectionSnippetSize

   val suffix = when {
      remainingItems <= 0 -> "]"
      else -> "] and $remainingItems more"
   }

   return joinToString(
      separator = ", ",
      prefix = "[",
      postfix = suffix,
      limit = MaxCollectionSnippetSize
   ) {
      recursiveRepr(this, it).value
   }.printed()
}
