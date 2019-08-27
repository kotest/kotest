package io.kotlintest.properties

import java.math.BigInteger
import kotlin.random.Random

class BigIntegerGen(maxNumBits: Int) : Gen<BigInteger> {
  private val numBitsGen: Gen<Int> = Gen.choose(0, maxNumBits)
  override fun constants(): Iterable<BigInteger> = emptyList()
  override fun random(random: Random?): Sequence<BigInteger> = numBitsGen.random(random).map { it.toBigInteger() }
}
