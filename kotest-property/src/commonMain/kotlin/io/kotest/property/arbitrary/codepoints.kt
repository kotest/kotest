package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * The minimum value of a
 * <a href="http://www.unicode.org/glossary/#code_point">
 * Unicode code point</a>, constant {@code U+0000}.
 */
internal const val MIN_CODE_POINT = 0x000000

/**
 * The maximum value of a
 * <a href="http://www.unicode.org/glossary/#code_point">
 * Unicode code point</a>, constant {@code U+10FFFF}.
 */
internal const val MAX_CODE_POINT = 0X10FFFF

internal const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000

fun Arb.Companion.codepoints(): Arb<Codepoint> = Arb.int(MIN_CODE_POINT..MAX_CODE_POINT).map { Codepoint(it) }
fun Arb.Companion.ascii(): Arb<Codepoint> = Arb.create(listOf(Codepoint('a'.toInt()))) { rs ->
   Arb.int(' '.toInt()..'z'.toInt()).map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.georgian() = Arb.create(listOf(Codepoint(0x10A0))) { rs ->
   val empty = listOf(0x10C6, 0x10ce, 0x10cf) + (0x10c8..0x10cC).toList()
   Arb.int(0x10A0..0x10FF).filterNot { it in empty }.map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.katakana() = Arb.create(listOf(Codepoint(0x30A1))) { rs ->
   Arb.int(0x30A0..0x30FF).map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.greekCoptic() = Arb.create(listOf(Codepoint(0x03B1))) { rs ->
   val empty = (0x0380..0x0383).toList() + listOf(0x0378, 0x0379, 0x038B, 0x038D, 0x03A2)
   Arb.int(0x0370..0x03FF).filterNot { it in empty }.map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.armenian() = Arb.create(listOf(Codepoint(0x0531))) { rs ->
   val empty = listOf(0x0557, 0x0558, 0x058B, 0x058C)
   Arb.int(0x0531..0x058F).filterNot { it in empty }.map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.hebrew() = Arb.create(listOf(Codepoint(0x05D0))) { rs ->
   val empty = (0x05c8..0x05cF).toList() + (0x05eB..0x05eE).toList()
   Arb.int(0x0591..0x05F4).filterNot { it in empty }.map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.arabic() = Arb.create(listOf(Codepoint(0x0627))) { rs ->
   val empty = listOf(0x062D)
   Arb.int(0x0600..0x06FF).filterNot { it in empty }.map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.cyrillic() = Arb.create(listOf(Codepoint(0x0430))) { rs ->
   Arb.int(0x0400..0x04FF).map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.hiragana() = Arb.create(listOf(Codepoint(0x3041))) { rs ->
   val empty = listOf(0x3097, 0x3098)
   Arb.int(0x3041..0x309F).filterNot { it in empty }.map { Codepoint(it) }.single(rs)
}

fun Arb.Companion.egyptianHieroglyphs() = Arb.create(listOf(Codepoint(0x13000))) { rs ->
   Arb.int(0x13000..0x1342E).map { Codepoint(it) }.single(rs)
}

data class Codepoint(val value: Int)

val Codepoint.isBmpCodePoint: Boolean
   get() = value ushr 16 == 0

val Codepoint.highSurrogate: Char
   get() = (value ushr 10).toChar() + (Char.MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10)).toInt()

val Codepoint.lowSurrogate: Char
   get() = (value ushr 0x3ff).toChar() + Char.MIN_LOW_SURROGATE.toInt()

fun Codepoint.asString(): String {
   return if (isBmpCodePoint) {
      value.toChar().toString()
   } else {
      String(
         charArrayOf(
            highSurrogate,
            lowSurrogate
         )
      )
   }
}
