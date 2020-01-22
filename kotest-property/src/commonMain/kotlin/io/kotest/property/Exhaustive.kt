package io.kotest.property

import kotlin.random.Random

/**
 * An [Exhaustive] is a type of [Argument] which generates an exhaustive set of values from a defined range.
 *
 * An example of an exhaustive is the sequence of integers from 0 to 100.
 * Another example is all strings of two characters.
 *
 * A exhaustive is useful when you want to generate all values from a given sample space,
 * rather than random values from that space. For example, if you were testing a
 * function that used an enum, you might prefer to guarantee that every enum value is used, rather
 * than selecting randomly from amongst the enum values (with possible duplicates and gaps).
 *
 * Exhaustives do not shrink their values. There is no need to find a smaller failing case because
 * the full sample space is being covered anyway.
 */
interface Exhaustive<T> : Argument<T> {

   fun values(): Sequence<T>

   override fun values(random: Random): Sequence<ArgumentValue<T>> =
      values().map { ArgumentValue(it, RTree.empty(it)) }

   companion object
}
