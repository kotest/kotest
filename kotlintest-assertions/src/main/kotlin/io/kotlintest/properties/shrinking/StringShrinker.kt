package io.kotlintest.properties.shrinking

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