package io.kotest.property

import kotlin.random.Random

/**
 * Models a particular value for an argument along with the reduced values for that input.
 */
data class ArgumentValue<out T>(val value: T, val shrinks: RTree<T>)

/**
 * A [Argument] is responsible for generating data to be used in property testing.
 * Each argument will provide data for a specific type <T>.
 *
 * There are two supported types of argument - [Arbitrary] and [Exhaustive] - defined
 * as sub interfaces of this interface.
 *
 * An arbitrary is used when you need random values across a large space.
 * An exhaustive is useful when you want exhaustive values from a small space.
 *
 * Both types of arguments can be mixed and matched in property tests. For example,
 * you could test a function with 1000 random positive integers (arbitrary) and every
 * even number from 0 to 200 (exhaustive).
 */
interface Argument<out T> {

   /**
    * @return the values provided by this [Argument] as a lazy list.
    *
    * @param random used for repeatable tests.
    */
   fun values(random: Random): Sequence<ArgumentValue<T>>
}

/**
 * Returns a new [Argument] which will merge the values from this argument and the values of
 * the supplied argument together in turn.
 *
 * In other words, if a provides 1,2,3 and b provides 7,8,9 then this argument will output
 * 1,7,2,8,3,9.
 *
 * The supplied argument must be a subtype of the type of this argument.
 *
 * @param other the gen to merge with this one
 * @return the merged gen.
 */
fun <T, U : T> Argument<T>.merge(other: Argument<U>): Argument<T> = object : Argument<T> {
   override fun values(random: Random): Sequence<ArgumentValue<T>> {
      return this@merge.values(random).zip(other.values(random)).flatMap { (a, b) ->
         sequenceOf(a, b)
      }
   }
}

/**
 * Returns a new [Argument] which will return the values from this arg and once values
 * of this arg are exhausted will return the values from the supplied arg.
 *
 * The supplied arg must be a subtype of the type of this arg.
 */
fun <T, U : T> Argument<T>.concat(other: Argument<U>): Argument<T> {
   return object : Argument<T> {
      override fun values(random: Random): Sequence<ArgumentValue<T>> =
         this@concat.values(random) + other.values(random)
   }
}
