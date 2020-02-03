package io.kotest.property.arbitrary

/**
 * The edge cases are [[Byte.MIN_VALUE], [Byte.MAX_VALUE], 0]
 */
fun Arb.Companion.byte() = arb(ByteShrinker, listOf(0, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
   it.nextBytes(1).first()
}

val ByteShrinker = IntShrinker.bimap({ it.toInt() }, { it.toByte() })
