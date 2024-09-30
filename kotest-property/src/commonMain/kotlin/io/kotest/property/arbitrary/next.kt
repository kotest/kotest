package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.checkAll
import io.kotest.property.forAll

/**
 * Draws a random value from this generator
 *
 * This method will draw a single value from the random values, that matches [predicate] (defaults to every
 * value)
 *
 * This expects that values will return an infinite, random sequence. Due to this, a call to [Sequence.first] is
 * made. As usually random is infinite, this should always return a different value. For fixed sequences, this will
 * always return the first value of the sequence.
 *
 * This is useful if you want a randomized value, but don't want to execute a property test over them (for example, by
 * using [checkAll] or [forAll]).
 *
 * IMPORTANT: This will not draw from the [edgecases] pool. Only random values.
 *
 * ```
 * val gen = Gen.string()
 * val generatedValue: String = gen.next()
 * val filteredValue: String = gen.next { it != "hello" }
 * ```
 */
fun <A> Arb<A>.next(predicate: (A) -> Boolean = { true }, rs: RandomSource = RandomSource.default()): A =
   filter(predicate).next(rs)
