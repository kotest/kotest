package io.kotlintest.properties.shrinking

object FloatShrinker : Shrinker<Float> {
  override fun shrink(failure: Float): List<Float> {
    return if (failure == 0F) emptyList() else {
      val a = listOf(0F, 1F, -1F, Math.abs(failure), failure / 3, failure / 2)
      val b = (1..5).map { failure - it }.reversed().filter { it > 0 }
      (a + b + Math.round(failure).toFloat()).distinct()
    }
  }
}
