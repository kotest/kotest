package io.kotest.properties.shrinking

class ChooseShrinker(val min: Int, val max: Int) : Shrinker<Int> {
  override fun shrink(failure: Int): List<Int> =
      when (failure) {
      // can't io.kotest.properties.shrinking.shrink further than the min value !
        min -> emptyList()
        else -> {
          val a = listOf(min, failure / 3, failure / 2, failure * 2 / 3)
          val b = (1..5).map { failure - it }.reversed()
          (a + b).distinct().filter { it >= min }
        }
      }
}
