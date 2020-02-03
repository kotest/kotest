package io.kotest.property.exhaustive

/**
 * Returns a [Exhaustive] of bytes in the given range.
 */
fun Exhaustive.Companion.bytes(from: Byte = Byte.MIN_VALUE, to: Byte = Byte.MAX_VALUE) = object : Exhaustive<Byte> {
   override val values: List<Byte> = (from..to).map { it.toByte() }
}
