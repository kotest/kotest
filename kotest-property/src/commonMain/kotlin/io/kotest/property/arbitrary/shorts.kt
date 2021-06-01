package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.random.nextInt
import kotlin.random.nextUInt

/**
 * Returns an [Arb] that produces [Short]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1, 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.short(min: Short = Short.MIN_VALUE, max: Short = Short.MAX_VALUE): Arb<Short> {
   val edges = shortArrayOf(min, -1, 0, 1, max).filter { it in min..max }.distinct()
   return arbitrary(edges, ShortShrinker) { it.random.nextInt(min..max).toShort() }
}

val ShortShrinker = IntShrinker(Short.MIN_VALUE..Short.MAX_VALUE).bimap({ it.toInt() }, { it.toShort() })

/**
 * Returns an [Arb] that positive produces [Short]s from 1 to [max] (inclusive).
 * The edge cases are 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.positiveShort(max: Short = Short.MAX_VALUE) = short(1, max)

/**
 * Returns an [Arb] that produces negative [Short]s from [min] to -1 (inclusive).
 * The edge cases are [min] and -1 which are only included if they are in the provided range.
 */
fun Arb.Companion.negativeShort(min: Short = Short.MIN_VALUE) = short(min, -1)

/**
 * Returns an [Arb] that produces [UShort]s from [min] to [max] (inclusive).
 * The edge cases are [min], 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.ushort(min: UShort = UShort.MIN_VALUE, max: UShort = UShort.MAX_VALUE): Arb<UShort> {
   val edges = listOf(min, 1u, max).filter { it in min..max }.distinct()
   return arbitrary(edges, UShortShrinker) { it.random.nextUInt(min..max).toUShort() }
}

val UShortShrinker = UIntShrinker(UShort.MIN_VALUE..UShort.MAX_VALUE).bimap({ it.toUInt() }, { it.toUShort() })
