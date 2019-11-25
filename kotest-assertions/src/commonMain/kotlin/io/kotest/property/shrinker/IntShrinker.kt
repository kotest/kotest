package io.kotest.property.shrinker

import io.kotest.property.PropertyInput
import kotlin.math.abs

object IntShrinker : Shrinker<Int> {
   override fun shrink(value: Int): List<PropertyInput<Int>> =
      when (value) {
         0 -> emptyList()
         1, -1 -> listOf(PropertyInput(0))
         else -> {
            val a = listOf(0, 1, -1, abs(value), value / 3, value / 2, value * 2 / 3)
            val b = (1..5).map { value - it }.reversed().filter { it > 0 }
            (a + b).distinct()
               .filterNot { it == value }
               .map { PropertyInput(it, this) }
         }
      }
}

