package io.kotest.properties.shrinking

import kotlin.math.abs
import kotlin.math.round

object FloatShrinker : Shrinker<Float> {
  override fun shrink(failure: Float): List<Float> {
    return if (failure == 0F) emptyList() else {
      val a = listOf(0F, 1F, -1F, abs(failure), failure / 3, failure / 2)
      val b = (1..5).map { failure - it }.reversed().filter { it > 0 }
      (a + b + round(failure)).distinct()
    }
  }
}
