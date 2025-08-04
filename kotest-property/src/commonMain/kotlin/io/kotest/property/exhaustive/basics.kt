package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

/**
 * Returns a [Exhaustive] of the two possible boolean values - true and false.
 */
fun Exhaustive.Companion.boolean(): Exhaustive<Boolean> = listOf(true, false).exhaustive()

/**
 * Returns a [Exhaustive] whose value is a single constant.
 */
fun <A> Exhaustive.Companion.constant(a: A): Exhaustive<A> = listOf(a).exhaustive()

fun Exhaustive.Companion.nullable(): Exhaustive<Nothing?> = listOf(null).exhaustive()

fun <A> Exhaustive<A>.andNull(): Exhaustive<A?> = (this.values + listOf(null)).exhaustive()

/**
 * Returns an Exhaustive which is the concatentation of this exhaustive values plus
 * the given exhaustive's values.
 */
operator fun <A> Exhaustive<A>.plus(other: Exhaustive<A>) = (this.values + other.values).exhaustive()

/**
 * Returns an Exhaustive which is the concatentation of this exhaustive values plus
 * the given exhaustive's values.
 *
 * Alias for [plus].
 */
fun <A> Exhaustive<A>.concat(other: Exhaustive<A>) = this + other

/**
 * Returns the cross product of two [Exhaustive]s.
 */
operator fun <A, B> Exhaustive<A>.times(other: Exhaustive<B>): Exhaustive<Pair<A, B>> {
   val values = this.values.flatMap { a ->
      other.values.map { b ->
         Pair(a, b)
      }
   }
   return values.exhaustive()
}
