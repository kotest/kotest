package io.kotest.property

import io.kotest.property.arbitrary.PropertyInput
import io.kotest.property.arbitrary.map
import kotlin.random.Random

/**
 * A Generator, or [Gen] is responsible for generating data to be used in property testing.
 * Each generator will generate data for a specific type <T>.
 *
 * There are two supported types of generators - [Arbitrary] and [Progression] - defined
 * as sub interfaces of this interface.
 *
 * An arbitrary is used when you need random values across a large space.
 * A progression is useful when you want exhaustive values from a small space.
 *
 * Both types of generators can be mixed and matched in property tests. For example,
 * you could test a function with 1000 random positive integers (arbitrary) and every
 * even number from 0 to 200 (progression).
 */
interface Gen<T> {

   /**
    * @return the values provided by this [Gen] as a lazy list.
    * @see [PropertyInput]
    */
   fun generate(random: Random): Sequence<PropertyInput<T>>
}

fun <T, U> Gen<T>.map(f: (T) -> U): Gen<U> = object : Gen<U> {
   override fun generate(random: Random): Sequence<PropertyInput<U>> =
      this@map.generate(random).map { it.map(f) }
}

fun <T> Gen<T>.filter(predicate: (T) -> Boolean): Gen<T> = object : Gen<T> {
   override fun generate(random: Random): Sequence<PropertyInput<T>> =
      this@filter.generate(random).filter { predicate(it.value) }
}

/**
 * Returns a new [Gen] which will merge the values from this gen and the values of
 * the supplied gen together in turn.
 *
 * In other words, if GenA provides 1,2,3 and GenB provides 7,8,9 then this gen will output
 * 1,7,2,8,3,9.
 *
 * The supplied gen must be a subtype of the type of this gen.
 *
 * @param other the gen to merge with this one
 * @return the merged gen.
 */
fun <T, U : T> Gen<T>.merge(other: Gen<U>): Gen<T> {
   return object : Gen<T> {
      override fun generate(random: Random): Sequence<PropertyInput<T>> =
         this@merge.generate(random).zip(other.generate(random)).flatMap { (a, b) ->
            sequenceOf(a, b)
         }
   }
}

/**
 * Returns a new [Gen]]which will return the values from this gen and once values
 * of this gen are exhausted will return the values from the supplied gen.
 * The supplied gen must be a subtype of the type of this gen.
 */
fun <T, U : T> Gen<T>.concat(other: Gen<U>): Gen<T> {
   return object : Gen<T> {
      override fun generate(random: Random): Sequence<PropertyInput<T>> =
         this@concat.generate(random) + other.generate(random)
   }
}
