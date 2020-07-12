package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.random.nextInt

/**
 * The edge cases are [[Short.MIN_VALUE], [Short.MAX_VALUE], 0]
 */
fun Arb.Companion.short() = arb(ShortShrinker, listOf(0, Short.MIN_VALUE, Short.MAX_VALUE)) {
   it.random.nextInt(Short.MIN_VALUE..Short.MAX_VALUE).toShort()
}

/**
 * The edge cases are [[Short.MIN_VALUE], [Short.MAX_VALUE], 0]
 */
@ExperimentalUnsignedTypes
fun Arb.Companion.ushort() = arb(UShortShrinker, listOf(0.toUShort(), UShort.MIN_VALUE, UShort.MAX_VALUE)) {
   it.random.nextInt().toUInt().shr(UInt.SIZE_BITS - UShort.SIZE_BITS).toUShort()
}

val ShortShrinker = IntShrinker(Short.MIN_VALUE..Short.MAX_VALUE).bimap({ it.toInt() }, { it.toShort() })

@OptIn(ExperimentalUnsignedTypes::class)
val UShortShrinker =
   IntShrinker(UShort.MIN_VALUE.toShort()..UShort.MAX_VALUE.toShort()).bimap({ it.toInt() }, { it.toUShort() })
