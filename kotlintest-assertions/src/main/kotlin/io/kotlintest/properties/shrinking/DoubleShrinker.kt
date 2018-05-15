package io.kotlintest.properties.shrinking

object DoubleShrinker : Shrinker<Double> {
  override fun shrink(failure: Double): List<Double> {
    return if (failure == 0.0) emptyList() else {
      val a = listOf(0.0, 1.0, -1.0, Math.abs(failure), failure / 3, failure / 2)
      val b = (1..5).map { failure - it }.reversed().filter { it > 0 }
      (a + b + Math.round(failure).toDouble()).distinct()
    }
  }
}
