package io.kotlintest.properties

import io.kotlintest.properties.shrinking.Shrinker

class ChooseShrinker(val min: Int, val max: Int) : Shrinker<Int> {
  override fun shrink(failure: Int): List<Int> =
      when (failure) {
      // can't shrink further than the min value !
        min -> emptyList()
        else -> {
          val a = listOf(min, failure / 3, failure / 2, failure * 2 / 3)
          val b = (1..10).map { failure - it }.reversed()
          (a + b).distinct().filter { it >= min }
        }
      }
}
