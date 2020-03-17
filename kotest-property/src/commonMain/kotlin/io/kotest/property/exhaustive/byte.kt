package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

/**
 * Returns a [Exhaustive] of bytes in the given range.
 */
fun Exhaustive.Companion.bytes(from: Byte = Byte.MIN_VALUE, to: Byte = Byte.MAX_VALUE) =
   (from..to).map { it.toByte() }.exhaustive()
