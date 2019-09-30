package io.kotest.properties.shrinking

import kotlin.math.abs

object IntShrinker : Shrinker<Int> {
  override fun shrink(failure: Int): List<Int> =
      when (failure) {
        0 -> emptyList()
        1, -1 -> listOf(0)
        else -> {
          val a = listOf(0, 1, -1, abs(failure), failure / 3, failure / 2, failure * 2 / 3)
          val b = (1..5).map { failure - it }.reversed().filter { it > 0 }
          (a + b).distinct().filterNot { it == failure }
        }
      }
}
