package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * The minimum value of a
 * <a href="http://www.unicode.org/glossary/#code_point">
 * Unicode code point</a>, constant {@code U+0000}.
 */
const val MIN_CODE_POINT = 0x000000

/**
 * The maximum value of a
 * <a href="http://www.unicode.org/glossary/#code_point">
 * Unicode code point</a>, constant {@code U+10FFFF}.
 */
const val MAX_CODE_POINT = 0X10FFFF

const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000

fun Arb.Companion.codepoints(): Arb<Codepoint> = arb { rs ->
   val ints = Arb.int(MIN_CODE_POINT..MAX_CODE_POINT)
   ints.values(rs).map { Codepoint(it.value) }
}

fun Arb.Companion.asciiCodepoints(): Arb<Codepoint> = arb { rs ->
   val ints = Arb.int('!'.toInt()..'z'.toInt())
   ints.values(rs).map { Codepoint(it.value) }
}

data class Codepoint(val value: Int)

fun Codepoint.isBmpCodePoint() = value ushr 16 == 0

fun Codepoint.highSurrogate(): Char {
   return (value ushr 10).toChar() + (Char.MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10)).toInt()
}

fun Codepoint.lowSurrogate(): Char {
   return (value ushr 0x3ff).toChar() + Char.MIN_LOW_SURROGATE.toInt()
}
