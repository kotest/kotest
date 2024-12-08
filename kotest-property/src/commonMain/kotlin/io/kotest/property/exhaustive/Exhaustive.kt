package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive
import kotlin.jvm.JvmName

/**
 * Returns an [Exhaustive] which provides the values from the given list.
 * @param `as` a non empty list.
 * @return [Exhaustive]
 * @throws [IllegalArgumentException] if the `as` is a empty list.
 */
fun <A> exhaustive(`as`: List<A>): Exhaustive<A> = `as`.exhaustive()

fun <A> Exhaustive.Companion.of(vararg elements: A): Exhaustive<A> = Exhaustive.collection(elements.asList())

/**
 * Returns an [Exhaustive] which provides the values from the receiver.
 * @return [Exhaustive]
 * @throws [IllegalArgumentException] if the receiver is a empty list.
 */
@JvmName("exhaustiveExt")
fun <A> List<A>.exhaustive(): Exhaustive<A> {
   require(this.isNotEmpty()) { "Can't build a Exhaustive for a empty list." }

   return object : Exhaustive<A>() {
      override val values = this@exhaustive
   }
}

/**
 * Returns a new [Exhaustive] which will merge the values from this Exhaustive and the values of
 * the supplied Exhaustive together, taking one from each in turn.
 *
 * In other words, if genA provides 1,2,3 and genB provides 7,8,9 then the merged
 * gen would output 1,7,2,8,3,9.
 *
 * The supplied gen and this gen must have a common supertype.
 *
 * @param other the arg to merge with this one
 * @return the merged arg.
 */
fun <A, B : A, C : A> Exhaustive<B>.merge(other: Exhaustive<C>): Exhaustive<A> = object : Exhaustive<A>() {
   override val values: List<A> = this@merge.values.zip(other.values).flatMap { listOf(it.first, it.second) }
}

/**
 * Returns a new [Exhaustive] which takes its elements from the receiver and filters
 * them using the supplied predicate.
 * In other words this exhaustive is a subset of the elements as determined by the filter.
 */
fun <A> Exhaustive<A>.filter(predicate: (A) -> Boolean) = object : Exhaustive<A>() {
   override val values: List<A> =
      this@filter.values.filter { predicate(it) }
}

/**
 * @return a new [Exhaustive] by filtering this Exhaustives output by the negated function [f]
 */
fun <A> Exhaustive<A>.filterNot(f: (A) -> Boolean): Exhaustive<A> = filter { !f(it) }

/**
 * Returns a new [Exhaustive] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Exhaustive<A>.map(f: (A) -> B): Exhaustive<B> = object : Exhaustive<B>() {
   override val values: List<B> =
      this@map.values.map { f(it) }
}

/**
 * Returns a new [Exhaustive] which takes its elements from the receiver and maps them using the supplied function.
 */
fun <A, B> Exhaustive<A>.flatMap(f: (A) -> Exhaustive<B>): Exhaustive<B> = object : Exhaustive<B>() {
   override val values: List<B> =
      this@flatMap.values.flatMap { f(it).values }
}

/**
 * Wraps a [Exhaustive] lazily. The given [f] is only evaluated once,
 * and not until the wrapper [Exhaustive] is evaluated.
 * */

fun <A> Exhaustive.Companion.lazy(f: () -> Exhaustive<A>): Exhaustive<A> {
   return object : Exhaustive<A>() {
      override val values: List<A> by kotlin.lazy { f().values }
   }
}
