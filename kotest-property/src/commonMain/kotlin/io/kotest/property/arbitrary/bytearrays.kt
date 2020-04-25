package io.kotest.property.arbitrary

import io.kotest.property.Arb
import io.kotest.property.Gen

/**
 * Returns an [Arb] that generates one dimension [ByteArray]s.
 *
 * @param generateArrayLength [Gen] to produce the size of the arrays
 * @param generateContents [Arb] to produce random bytes as the values for the array.
 */
fun Arb.Companion.byteArrays(generateArrayLength: Gen<Int>, generateContents: Arb<Byte>): Arb<ByteArray> {
   return arb { rs ->
      val lengths = generateArrayLength.generate(rs).iterator()
      val bytes = generateContents.values(rs).iterator()
      sequence<ByteArray> {
         val length = lengths.next().value
         ByteArray(length) { bytes.next().value }
      }
   }
}
