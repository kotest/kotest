package io.kotest.property.shrinker

import io.kotest.property.PropertyInput
import kotlin.math.abs
import kotlin.math.round

object DoubleShrinker : Shrinker<Double> {
   override fun shrink(value: Double): List<PropertyInput<Double>> {
      return if (value == 0.0) emptyList() else {
         val a = listOf(0.0, 1.0, -1.0, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value)).distinct().map { PropertyInput(it, this) }
      }
   }
}
