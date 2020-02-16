package io.kotest.property.arbitrary

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


val ShortShrinker = IntShrinker.bimap({ it.toInt() }, { it.toShort() })
val UShortShrinker = IntShrinker.bimap({ it.toInt() }, { it.toUShort() })
