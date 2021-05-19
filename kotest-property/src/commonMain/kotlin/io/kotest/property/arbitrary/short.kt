package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.random.nextInt

/**
 * The edge cases are [[Short.MIN_VALUE], [Short.MAX_VALUE], 0, -1, 1]
 */
fun Arb.Companion.short() = arbitrary(listOf(0, -1, 1, Short.MIN_VALUE, Short.MAX_VALUE), ShortShrinker) {
   it.random.nextInt(Short.MIN_VALUE..Short.MAX_VALUE).toShort()
}

/**
 * The edge cases are [[Short.MIN_VALUE], [Short.MAX_VALUE], 0, -1, 1]
 */
@ExperimentalUnsignedTypes
fun Arb.Companion.ushort() =
   arbitrary(listOf(0.toUShort(), 1.toUShort(), UShort.MIN_VALUE, UShort.MAX_VALUE), UShortShrinker) {
      it.random.nextInt().toUInt().shr(UInt.SIZE_BITS - UShort.SIZE_BITS).toUShort()
   }

val ShortShrinker = IntShrinker(Short.MIN_VALUE..Short.MAX_VALUE).bimap({ it.toInt() }, { it.toShort() })

@ExperimentalUnsignedTypes
val UShortShrinker =
   IntShrinker(UShort.MIN_VALUE.toShort()..UShort.MAX_VALUE.toShort()).bimap({ it.toInt() }, { it.toUShort() })
