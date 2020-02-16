package io.kotest.assertions.show

/**
 * A simple implementation of [Show] that handles null values,
 * and for non-null just delegates to the toString method of the class.
 */
object DefaultShow : Show<Any?> {
  override fun show(a: Any?): String = when (a) {
    null -> "<null>"
    else -> a.toString()
  }
}
