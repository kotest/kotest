package io.kotest.properties

import java.math.BigInteger

class BigIntegerGen(maxNumBits: Int) : Gen<BigInteger> {
  private val numBitsGen: Gen<Int> = Gen.choose(0, maxNumBits)
  override fun constants(): Iterable<BigInteger> = emptyList()
  override fun random(seed: Long?): Sequence<BigInteger> = numBitsGen.random(seed).map { it.toBigInteger() }
}
