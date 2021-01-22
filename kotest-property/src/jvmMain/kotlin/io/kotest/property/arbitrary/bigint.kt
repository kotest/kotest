package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.math.BigInteger
import kotlin.random.asJavaRandom

/**
 * Generates random BigIntegers from the [range]
 */
fun Arb.Companion.bigInt(range: IntRange) = int(range).map { it.toBigInteger() }

/**
 * Generate random BigIntegers with bits ranging from 0 to [maxNumBits]
 */
fun Arb.Companion.bigInt(maxNumBits: Int) = bigInt(0, maxNumBits)

/**
 * Generate random BigIntegers with bits ranging from [minNumBits] to [maxNumBits]
 */
fun Arb.Companion.bigInt(minNumBits: Int, maxNumBits: Int): Arb<BigInteger> {
   return arbitrary { rs ->
      val numBits = Arb.int(minNumBits, maxNumBits).next(rs)
      BigInteger(numBits, rs.random.asJavaRandom())
   }
}
