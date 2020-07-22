package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.math.BigInteger

fun Arb.Companion.bigInt(range: IntRange) = Arb.int(range).map { it.toBigInteger() }

fun Arb.Companion.bigInt(maxNumBits: Int): Arb<BigInteger> {
   return arb { rs ->
      val numBitsGen = Arb.int(0, maxNumBits).values(rs).iterator()
      sequence {
         val bigint = BigInteger.ZERO
         repeat(numBitsGen.iterator().next().value) { if (rs.random.nextBoolean()) bigint.setBit(it) }
         yield(bigint)
      }
   }
}
