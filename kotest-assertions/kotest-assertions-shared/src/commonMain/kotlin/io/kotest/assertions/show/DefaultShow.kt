package io.kotest.assertions.show

/**
 * A default implementation of [Show] that handles primitives as well as nullable values.
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
