package io.kotest.property

import io.kotest.property.arbitrary.PropertyInput
import kotlin.random.Random

/**
 * A [Progression] is a type of [Gen] which generates an exhaustive set of values from a defined range.
 *
 * An example of a progression is the sequence of integers from 0 to 100.
 * Another example is all strings of two characters.
 *
 * A progression is useful when you want to generate an exhaustive set of values from a given
 * sample space, rather than random values from that space. For example, if you were testing a
 * function that used an enum, you might prefer to guarantee that every enum value is used, rather
 * than selecting randomly from amongst the enum values (with possible duplicates and gaps).
 *
 * Progressions do not shrink their values. There is no need to find a smaller failing case, because
 * the smaller values will themselves naturally be included in the tested values.
 *
 * A progression is less suitable when you have a large sample space you need to select values from.
 */
interface Progression<T> : Gen<T> {

   /**
    * @return the values for this progression as a lazy list.
    */
   fun values(): Sequence<T>

   override fun generate(random: Random): Sequence<PropertyInput<T>> = values().map { PropertyInput(it) }

   companion object
}
