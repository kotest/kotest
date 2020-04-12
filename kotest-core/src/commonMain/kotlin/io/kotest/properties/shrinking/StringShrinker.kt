package io.kotest.properties.shrinking

object StringShrinker : Shrinker<String> {
  override fun shrink(failure: String): List<String> = when (failure.length) {
    0 -> emptyList()
    1 -> listOf("", "a")
    else -> {
      val first = failure.take(failure.length / 2 + failure.length % 2)
      val second = failure.takeLast(failure.length / 2)
      // always include empty string as the best io.kotest.properties.shrinking.shrink
     listOf("", first, first.padEnd(failure.length, 'a'), second, second.padStart(failure.length, 'a'))
    }
  }
}
