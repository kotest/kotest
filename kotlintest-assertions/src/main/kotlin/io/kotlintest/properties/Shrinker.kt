package io.kotlintest.properties

interface Shrinker<T> {

  /**
   * Given a value, T, this function returns possible "smaller"
   * values to be used as candidates for shrinking.
   *
   * A smaller value is defined by the Shrinker. For a string it may
   * be considered a "simpler" string (one with less duplication), and/or
   * one with less characters. For an integer it is typically
   * considered a smaller value with a positive sign.
   *
   * If the value cannot be shrunk further, or the type
   * does not support meaningful shrinking, then this function should
   * return an empty list.
   */
  fun shrink(failure: T): List<T>
}

object StringShrinker : Shrinker<String> {
  override fun shrink(failure: String): List<String> {
    // nothing more we can do with an empty string
    return if (failure.isEmpty()) emptyList() else {
      // 5 smaller strings and 5 strings with prefixes replaced
      val smaller = (1..5).map { failure.dropLast(it) }.filter { it.isNotEmpty() }.reversed()
      val prefixs = smaller.map { it.padStart(failure.length, 'a') }
      // always include empty string as the best shrink
      listOf("") + smaller + prefixs
    }
  }
}