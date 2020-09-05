package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.Gen

/**
 * Returns an [Arb] that generates one dimension [ByteArray]s.
 *
 * @param generateArrayLength [Gen] to produce the size of the arrays
 * @param generateContents [Arb] to produce random bytes as the values for the array.
 */
fun Arb.Companion.byteArrays(generateArrayLength: Gen<Int>, generateContents: Arb<Byte>): Arb<ByteArray> {
   val arbLength: Arb<Int> = when (generateArrayLength) {
      is Arb -> generateArrayLength
      is Exhaustive -> generateArrayLength.toArb()
   }

   return arbLength.flatMap { length ->
      val edge = sequence { yieldAll(generateContents.edgecases()) }.take(length).toList().toByteArray()
      Arb.create(listOf(edge)) { rs ->
         generateContents.take(length, rs).toList().toByteArray()
      }
   }
}
