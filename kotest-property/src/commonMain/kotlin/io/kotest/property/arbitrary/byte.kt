package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.random.nextUInt

/**
 * Returns an [Arb] that produces [Byte]s, where the edge cases are [min], -1, 0, 1 and [max].
 * -1, 0 and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.byte(min: Byte = Byte.MIN_VALUE, max: Byte = Byte.MAX_VALUE): Arb<Byte> {
   val edges = byteArrayOf(min, -1, 0, 1, max).filter { it in min..max }.distinct()
   return arbitrary(edges, ByteShrinker) {
      generateSequence { it.random.nextBytes(1).first() }.filter { it in min..max }.first()
   }
}

val ByteShrinker = IntShrinker(Byte.MIN_VALUE..Byte.MAX_VALUE).bimap({ it.toInt() }, { it.toByte() })

/**
 * Returns an [Arb] that produces positives [Byte]s,  where the edge cases are 1 and [max].
 */
fun Arb.Companion.positiveBytes(max: Byte = Byte.MAX_VALUE) = byte(1, max)

/**
 * Returns an [Arb] that produces negative [Byte]s, where the edge cases are [min] and -1 and 0.
 */
fun Arb.Companion.negativeBytes(min: Byte = Byte.MIN_VALUE) = byte(min, 0)

/**
 * Returns an [Arb] that produces [UByte]s, where the edge cases are [min], 1 and [max].
 * 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.ubyte(min: UByte = UByte.MIN_VALUE, max: UByte = UByte.MAX_VALUE): Arb<UByte> {
   val edges = listOf(min, 1u, max).filter { it in min..max }.distinct()
   return arbitrary(edges, UByteShrinker) { it.random.nextUInt(min..max).toUByte() }
}

val UByteShrinker = UIntShrinker(UByte.MIN_VALUE..UByte.MAX_VALUE).bimap({ it.toUInt() }, { it.toUByte() })
