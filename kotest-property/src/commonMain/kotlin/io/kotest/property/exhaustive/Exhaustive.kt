package io.kotest.property.exhaustive

import io.kotest.property.Gen
import io.kotest.property.Sample
import kotlin.random.Random

/**
 * An exhaustive is a type of [Gen] which generates an exhaustive set of values from a defined range.
 *
 * An example of a exhaustive is the sequence of integers from 0 to 100.
 * Another example is all strings of two characters.
 *
 * A progression is useful when you want to generate an exhaustive set of values from a given
 * sample space, rather than random values from that space. For example, if you were testing a
 * function that used an enum, you might prefer to guarantee that every enum value is used, rather
 * than selecting randomly from amongst the enum values (with possible duplicates and gaps).
 *
 * Exhaustives do not shrink their values. There is no need to find a smaller failing case, because
 * the smaller values will themselves naturally be included in the tested values.
 *
 * An exhaustive is less suitable when you have a large sample space you need to select values from.
 */
interface Exhaustive<A> : Gen<A> {

   /**
    * Returns the values of this [Exhaustive].
    */
   val values: List<A>

   override fun minIterations(): Int = values.size

   override fun generate(random: Random): Sequence<Sample<A>> = generateSequence { values.map { Sample(it) } }.flatten()

   companion object
}
