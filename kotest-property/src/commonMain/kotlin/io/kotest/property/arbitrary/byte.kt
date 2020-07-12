package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Returns an [Arb] that produces [Byte]s, where the edge cases are 0, -1, 1, min and max.
 * 0, -1, and 1 will only be included if they are within the specified min and max bounds.
 */
fun Arb.Companion.byte(min: Byte = Byte.MIN_VALUE, max: Byte = Byte.MAX_VALUE): Arb<Byte> {
   val edges = byteArrayOf(1, -1, 0, min, max).filter { it in min..max }.distinct()
   return arb(ByteShrinker, edges) {
      generateSequence { it.random.nextBytes(1).first() }.filter { it in min..max }.first()
   }
}

val ByteShrinker = IntShrinker(Byte.MIN_VALUE..Byte.MAX_VALUE).bimap({ it.toInt() }, { it.toByte() })
