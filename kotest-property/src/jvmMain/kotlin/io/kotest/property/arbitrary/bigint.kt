package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.math.BigInteger

fun Arb.Companion.bigInt(range: IntRange) = Arb.int(range).map { it.toBigInteger() }

fun Arb.Companion.bigInt(maxNumBits: Int): Arb<BigInteger> {
   return arbitrary { rs ->
      val numBits = Arb.int(0, maxNumBits).next(rs)
      val bigint = BigInteger.ZERO
      repeat(numBits) { if (rs.random.nextBoolean()) bigint.setBit(it) }
      bigint
   }
}
