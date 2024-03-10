package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Returns an [Arb] where each generated value is an IP Address in V4 format represented as a String.
 * Each part is a number between 0 and 255 except for the first part which is 1-255.
 */
fun Arb.Companion.ipAddressV4(): Arb<String> = arbitrary {
   listOf(
      it.random.nextInt(1, 255),
      it.random.nextInt(0, 255),
      it.random.nextInt(0, 255),
      it.random.nextInt(0, 255)
   ).joinToString(".")
}

/**
 * Returns an [Arb] where each generated value is an IP Address in V6 format represented as a String.
 * Each part is a number between 0 and 65535 represented as a hexadecimal number.
 */
fun Arb.Companion.ipAddressV6(): Arb<String> = arbitrary {
   listOf(
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase(),
      it.random.nextInt(0x10000).toString(16).uppercase()
   ).joinToString(":")
}
