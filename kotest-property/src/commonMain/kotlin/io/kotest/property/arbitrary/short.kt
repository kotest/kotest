package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.random.nextInt
import kotlin.random.nextUInt

/**
 * Returns an [Arb] that produces [Short]s, where the edge cases are [min], -1, 0, 1 and [max].
 * -1, 0 and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.short(min: Short = Short.MIN_VALUE, max: Short = Short.MAX_VALUE): Arb<Short> {
   val edges = shortArrayOf(min, -1, 0, 1, max).filter { it in min..max }.distinct()
   return arbitrary(edges, ShortShrinker) { it.random.nextInt(min..max).toShort() }
}

val ShortShrinker = IntShrinker(Short.MIN_VALUE..Short.MAX_VALUE).bimap({ it.toInt() }, { it.toShort() })

/**
 * Returns an [Arb] that produces positives [Short]s,  where the edge cases are 1 and [max].
 */
fun Arb.Companion.positiveShorts(max: Short = Short.MAX_VALUE) = short(1, max)

/**
 * Returns an [Arb] that produces negative [Short]s, where the edge cases are [min] and -1 and 0.
 */
fun Arb.Companion.negativeShorts(min: Short = Short.MIN_VALUE) = short(min, 0)

/**
 * Returns an [Arb] that produces [UShort]s, where the edge cases are [min], 1 and [max].
 * 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.ushort(min: UShort = UShort.MIN_VALUE, max: UShort = UShort.MAX_VALUE): Arb<UShort> {
   val edges = listOf(min, 1u, max).filter { it in min..max }.distinct()
   return arbitrary(edges, UShortShrinker) { it.random.nextUInt(min..max).toUShort() }
}

val UShortShrinker = UIntShrinker(UShort.MIN_VALUE..UShort.MAX_VALUE).bimap({ it.toUInt() }, { it.toUShort() })
