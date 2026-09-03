package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.Shrinker
import io.kotest.property.bimap
import kotlin.random.nextInt
import kotlin.random.nextUInt

/**
 * Returns an [Arb] that produces [Short]s from [min] to [max] (inclusive).
 * The edge cases are [min], -1, 0, 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.short(min: Short = Short.MIN_VALUE, max: Short = Short.MAX_VALUE): Arb<Short> {
   val edges = shortArrayOf(min, -1, 0, 1, max).filter { it in min..max }.distinct()
   // The shrinker is bounded by the same range as the generator, so that shrunk candidates
   // stay inside the domain the arb can actually produce.
   return arbitrary(edges, shortShrinker(min, max)) { it.random.nextInt(min..max).toShort() }
}

private fun shortShrinker(min: Short, max: Short): Shrinker<Short> =
   IntShrinker(min.toInt()..max.toInt()).bimap({ it.toInt() }, { it.toShort() })

/**
 * A [Shrinker] for [Short]s bounded by the full [Short] range.
 *
 * Note this shrinker is not aware of any narrower bounds - [Arb.short] builds its own shrinker
 * bounded by the range it was given so that shrunk values stay inside that range.
 */
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
 * Returns an [Arb] that produces [ShortArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
fun Arb.Companion.shortArray(length: Gen<Int>, content: Arb<Short>): Arb<ShortArray> =
   toPrimitiveArray(length, content, Collection<Short>::toShortArray)


/**
 * Returns an [Arb] that produces [UShort]s from [min] to [max] (inclusive).
 * The edge cases are [min], 1 and [max] which are only included if they are in the provided range.
 */
fun Arb.Companion.uShort(min: UShort = UShort.MIN_VALUE, max: UShort = UShort.MAX_VALUE): Arb<UShort> {
   val edges = listOf(min, 1u, max).filter { it in min..max }.distinct()
   // The shrinker is bounded by the same range as the generator, so that shrunk candidates
   // stay inside the domain the arb can actually produce.
   return arbitrary(edges, uShortShrinker(min, max)) { it.random.nextUInt(min..max).toUShort() }
}

private fun uShortShrinker(min: UShort, max: UShort): Shrinker<UShort> =
   UIntShrinker(min.toUInt()..max.toUInt()).bimap({ it.toUInt() }, { it.toUShort() })

/**
 * A [Shrinker] for [UShort]s bounded by the full [UShort] range.
 *
 * Note this shrinker is not aware of any narrower bounds - [Arb.uShort] builds its own shrinker
 * bounded by the range it was given so that shrunk values stay inside that range.
 */
val UShortShrinker = UIntShrinker(UShort.MIN_VALUE..UShort.MAX_VALUE).bimap({ it.toUInt() }, { it.toUShort() })

/**
 * Returns an [Arb] that produces [UShortArray]s where [length] produces the length of the arrays and
 * [content] produces the content of the arrays.
 */
@ExperimentalUnsignedTypes
fun Arb.Companion.uShortArray(length: Gen<Int>, content: Arb<UShort>): Arb<UShortArray> =
   toPrimitiveArray(length, content, Collection<UShort>::toUShortArray)
