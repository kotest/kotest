package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RTree
import io.kotest.property.RandomSource
import io.kotest.property.Sample

/**
 * Creates an [Arb] which will produce null values.
 *
 * The probability of the arb producing an null is based on the nullProbability parameter, which
 * should be between 0.0 and 1.0 (values outside this range will throw an IllegalArgumentException).
 * Higher values increase the chance of a null being generated.
 *
 * The edge cases will also include the previous [Arb]s edge cases plus a null.
 *
 * @throws IllegalArgumentException when a nullProbability value outside the range of 0.0 to 1.0 is provided.
 * @returns an arb<A?> that can produce null values.
 */
fun <A> Arb<A>.orNull(nullProbability: Double): Arb<A?> {
   require(nullProbability in 0.0..1.0) {
      "Please specify a null probability between 0.0 and 1.0. $nullProbability was provided."
   }
   return orNull { rs: RandomSource -> rs.random.nextDouble(0.0, 1.0) <= nullProbability }
}

/**
 * Creates an [Arb] which will produce null values.
 *
 * The probability of the arb producing an null is based on the isNextNull function.
 * By default this uses a random boolean so should result in roughly half nulls,
 * half values from the source arb.
 *
 * @returns an Arb<A?> that can produce null values.
 */
fun <A> Arb<A>.orNull(isNextNull: (RandomSource) -> Boolean = { it.random.nextBoolean() }): Arb<A?> =
   object : Arb<A?>() {
      override fun edgecase(rs: RandomSource): Sample<A>? = if (isNextNull(rs)) null else this@orNull.edgecase(rs)

      override fun sample(rs: RandomSource): Sample<A?> {
         val baseSample = if (isNextNull(rs)) Sample(null) else this@orNull.sample(rs)
         return baseSample.copy(
            shrinks = RTree(
               baseSample.shrinks.value,
               kotlin.lazy { listOf(RTree({ null })) + baseSample.shrinks.children.value })
         )
      }
   }
