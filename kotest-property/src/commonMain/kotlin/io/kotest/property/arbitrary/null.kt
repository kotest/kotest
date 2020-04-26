package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource

/**
 * Creates an Arb which will produce null values.
 *
 * The probability of the arb producing an null is based on the nullProbability parameter, which
 * should be between 0.0 and 1.0 (values outside this range will throw an IllegalArgumentException).
 * Higher values increase the chance of a null being generated.
 *
 * The edgecases will also include the previous arbs edgecases plus a null.
 *
 * @throws IllegalArgumentException when a nullProbability value outside the range of 0.0 to 1.0 is provided.
 * @returns an arb<A?> that can produce null values.
 */
fun <A> Arb<A>.orNull(nullProbability: Double): Arb<A?> {
   require(nullProbability >= 0 && nullProbability <= 1) {
      "Please specify a null probability between 0.0 and 1.0. $nullProbability was provided."
   }
   return orNull { rs: RandomSource -> rs.random.nextDouble(0.0, 1.0) <= nullProbability }
}

/**
 * Creates an Arb which will produce null values.
 *
 * The probability of the arb producing an null is based on the isNextNull function. By default this is determined by
 * a random boolean.
 *
 * The edgecases will also include the previous arbs edgecases plus a null.
 *
 * @returns an arb<A?> that can produce null values.
 */
fun <A> Arb<A>.orNull(isNextNull: (RandomSource) -> Boolean = { it.random.nextBoolean() }): Arb<A?> =
   arb(this.edgecases().plus(null as A?)) { rs ->
      sequence {
         while (true)
            yield(if (isNextNull(rs)) null else this@orNull.next(rs))
      }
   }
