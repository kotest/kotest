package io.kotest.property.arbitrary

/**
 * Returns an [Arb] which returns the sample values in the same order as they are passed in,
 * once all sample values are used it repeats elements from start.
 */
fun <A> Arb.Companion.samples(vararg sampleValues: A): Arb<A> {
   require(sampleValues.isNotEmpty())

   var currentIndex = 0

   fun getNextSampleElementProvider(): A {
      val nextIndex = currentIndex % sampleValues.size
      val nextValue = sampleValues[nextIndex]
      currentIndex += 1
      return nextValue
   }

   return arb(sampleValues.asList()) {
      getNextSampleElementProvider()
   }
}
