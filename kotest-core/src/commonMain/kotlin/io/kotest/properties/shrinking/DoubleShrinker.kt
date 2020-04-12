package io.kotest.properties.shrinking

import kotlin.math.abs
import kotlin.math.round

object DoubleShrinker : Shrinker<Double> {
  override fun shrink(failure: Double): List<Double> {
    return if (failure == 0.0) emptyList() else {
      val a = listOf(0.0, 1.0, -1.0, abs(failure), failure / 3, failure / 2)
      val b = (1..5).map { failure - it }.reversed().filter { it > 0 }
      (a + b + round(failure)).distinct()
    }
  }
}
