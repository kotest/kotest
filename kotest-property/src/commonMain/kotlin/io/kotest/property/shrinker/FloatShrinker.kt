package io.kotest.property.shrinker

import io.kotest.property.PropertyInput
import kotlin.math.abs
import kotlin.math.round

object FloatShrinker : Shrinker<Float> {
   override fun shrink(value: Float): List<PropertyInput<Float>> {
      return if (value == 0F) emptyList() else {
         val a = listOf(0F, 1F, -1F, abs(value), value / 3, value / 2)
         val b = (1..5).map { value - it }.reversed().filter { it > 0 }
         (a + b + round(value))
            .distinct()
            .map { PropertyInput(it, FloatShrinker) }
      }
   }
}
