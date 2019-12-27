package io.kotest.property.arbitraries

import io.kotest.property.Arbitrary
import io.kotest.property.PropertyInput
import io.kotest.property.Shrinker
import kotlin.random.Random

/**
 * Returns an [Arbitrary] where each generated value is a multiple of k between 0 to the specified max.
 *
 * The edge cases are 0, since 0 is a multiple of every integer.
 *
 * This arbitrary uses a [Shrinker] which will reduce the size of a failing value by removing
 * factors of k until 0 is hit.
 *
 */
fun Arbitrary.Companion.multiples(iterations: Int = 1000, k: Int, max: Int): Arbitrary<Int> = object : Arbitrary<Int> {

   // 0 is a multiple of everything
   override fun edgecases(): Iterable<Int> = listOf(0)

   override fun samples(random: Random): Sequence<PropertyInput<Int>> {
      return generateSequence { random.nextInt(max / k) * k }
         .filter { it >= 0 }
         .take(iterations)
         .map { PropertyInput(it, MultipleShrinker(k)) }
   }
}

class MultipleShrinker(private val multiple: Int) : Shrinker<Int> {
   override fun shrink(value: Int): List<PropertyInput<Int>> = when (value) {
      0 -> emptyList()
      in 1..multiple -> listOf(PropertyInput(0))
      else -> listOf(PropertyInput(value - multiple, this))
   }
}
