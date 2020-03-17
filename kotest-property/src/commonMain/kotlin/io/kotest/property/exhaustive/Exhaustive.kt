package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive
import kotlin.jvm.JvmName

/**
 * Returns an [Exhaustive] which provides the values from the given list.
 */
fun <A> exhaustive(`as`: List<A>): Exhaustive<A> = object : Exhaustive<A>() {
   override val values: List<A> = `as`
}

@JvmName("exhaustiveExt")
fun <A> List<A>.exhaustive(): Exhaustive<A> = object : Exhaustive<A>() {
   override val values: List<A> = this@exhaustive
}

/**
 * Returns a new [Exhaustive] which will merge the values from this Exhaustive and the values of
 * the supplied Exhaustive together, taking one from each in turn.
 *
 * In other words, if genA provides 1,2,3 and genB provides 7,8,9 then the merged
 * gen would output 1,7,2,8,3,9.
 *
 * The supplied gen must be a subtype of the type of this gen.
 *
 * @param other the arg to merge with this one
 * @return the merged arg.
 */

fun <A, B : A> Exhaustive<A>.merge(other: Exhaustive<B>): Exhaustive<A> = object : Exhaustive<A>() {
   override val values: List<A> = this@merge.values.zip(other.values).flatMap { listOf(it.first, it.second) }
}
