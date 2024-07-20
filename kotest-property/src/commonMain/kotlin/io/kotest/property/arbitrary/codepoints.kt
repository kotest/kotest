package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * The minimum value of a Unicode code point, constant {@code U+000000}.
 * https://www.unicode.org/glossary/#code_point
 */
internal const val MIN_CODE_POINT = 0x000000

/**
 * The maximum value of a Unicode code point, constant {@code U+10FFFF}.
 * https://www.unicode.org/glossary/#code_point
 */
internal const val MAX_CODE_POINT = 0X10FFFF

internal const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000

fun Arb.Companion.codepoints(): Arb<Codepoint> =
   Arb.of((MIN_CODE_POINT..MAX_CODE_POINT).map(::Codepoint))
      .withEdgecases(Codepoint(MIN_CODE_POINT), Codepoint(MAX_CODE_POINT))

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.az()")
)
fun Arb.Companion.az() = Codepoint.az()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.alphanumeric()")
)
fun Arb.Companion.alphanumeric(): Arb<Codepoint> =
   Codepoint.alphanumeric()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.ascii()")
)
fun Arb.Companion.ascii(): Arb<Codepoint> =
   Codepoint.ascii()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.georgian()")
)
fun Arb.Companion.georgian(): Arb<Codepoint> =
   Codepoint.georgian()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.katakana()")
)
fun Arb.Companion.katakana(): Arb<Codepoint> =
   Codepoint.katakana()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.greekCoptic()")
)
fun Arb.Companion.greekCoptic(): Arb<Codepoint> =
   Codepoint.greekCoptic()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.armenian()")
)
fun Arb.Companion.armenian(): Arb<Codepoint> =
   Codepoint.armenian()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.hebrew()")
)
fun Arb.Companion.hebrew(): Arb<Codepoint> =
   Codepoint.hebrew()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.arabic()")
)
fun Arb.Companion.arabic(): Arb<Codepoint> =
   Codepoint.arabic()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.cyrillic()")
)
fun Arb.Companion.cyrillic(): Arb<Codepoint> =
   Codepoint.cyrillic()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.hiragana()")
)
fun Arb.Companion.hiragana(): Arb<Codepoint> =
   Codepoint.hiragana()

@Deprecated(
   "Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.egyptianHieroglyphs()")
)
fun Arb.Companion.egyptianHieroglyphs(): Arb<Codepoint> =
   Codepoint.egyptianHieroglyphs()

fun Codepoint.Companion.az(): Arb<Codepoint> =
   Arb.of(('a'.code..'z'.code).map(::Codepoint))
      .withEdgecases(Codepoint('a'.code))

fun Codepoint.Companion.alphanumeric(): Arb<Codepoint> =
   Arb.of((('a'..'z') + ('A'..'Z') + ('0'..'9')).map { Codepoint(it.code) })

/**
 * Returns an [Arb] that generates ASCII codepoints including control characters(0-127).
 */
fun Codepoint.Companion.ascii(): Arb<Codepoint> =
   Arb.of((0..127).map(::Codepoint))
      .withEdgecases((0..31).map(::Codepoint))

/**
 * Returns an [Arb] that generates **printable** ASCII codepoints(32-126).
 */
fun Codepoint.Companion.printableAscii(): Arb<Codepoint> =
   Arb.of((' '.code..'~'.code).map(::Codepoint))
      .withEdgecases(Codepoint('a'.code))

/**
 * Returns an [Arb] that generates HEX codepoints.
 */
fun Codepoint.Companion.hex(): Arb<Codepoint> =
   Arb.of((('a'.code..'f'.code) + ('0'.code..'9'.code)).map(::Codepoint))

fun Codepoint.Companion.georgian(): Arb<Codepoint> {
   val empty = listOf(0x10C6, 0x10ce, 0x10cf) + (0x10c8..0x10cC).toList()
   val codepoints = (0x10A0..0x10FF).filterNot { it in empty }.map(::Codepoint)
   return Arb.of(codepoints).withEdgecases(Codepoint(0x10A0))
}

fun Codepoint.Companion.katakana(): Arb<Codepoint> =
   Arb.of((0x30A0..0x30FF).map(::Codepoint))
      .withEdgecases(Codepoint(0x30A1))

fun Codepoint.Companion.greekCoptic(): Arb<Codepoint> {
   val empty = (0x0380..0x0383).toList() + listOf(0x0378, 0x0379, 0x038B, 0x038D, 0x03A2)
   val codepoints = (0x0370..0x03FF).filterNot { it in empty }.map(::Codepoint)
   return Arb.of(codepoints).withEdgecases(Codepoint(0x03B1))
}

fun Codepoint.Companion.armenian(): Arb<Codepoint> {
   val empty = listOf(0x0557, 0x0558, 0x058B, 0x058C)
   val codepoints = (0x0531..0x058F).filterNot { it in empty }.map(::Codepoint)
   return Arb.of(codepoints).withEdgecases(Codepoint(0x0531))
}

fun Codepoint.Companion.hebrew(): Arb<Codepoint> {
   val empty = (0x05c8..0x05cF).toList() + (0x05eB..0x05eE).toList()
   val codepoints = (0x0591..0x05F4).filterNot { it in empty }.map(::Codepoint)
   return Arb.of(codepoints).withEdgecases(Codepoint(0x05D0))
}

fun Codepoint.Companion.arabic(): Arb<Codepoint> {
   val empty = listOf(0x062D)
   val codepoints = (0x0600..0x06FF).filterNot { it in empty }.map(::Codepoint)
   return Arb.of(codepoints).withEdgecases(Codepoint(0x0627))
}

fun Codepoint.Companion.cyrillic(): Arb<Codepoint> =
   Arb.of((0x0400..0x04FF).map(::Codepoint)).withEdgecases(Codepoint(0x0430))

fun Codepoint.Companion.hiragana(): Arb<Codepoint> {
   val empty = listOf(0x3097, 0x3098)
   val codepoints = (0x3041..0x309F).filterNot { it in empty }.map(::Codepoint)
   return Arb.of(codepoints).withEdgecases(Codepoint(0x3041))
}

fun Codepoint.Companion.egyptianHieroglyphs(): Arb<Codepoint> =
   Arb.of((0x13000..0x1342E).map(::Codepoint))
      .withEdgecases(Codepoint(0x13000))

fun Codepoint.Companion.whitespace(): Arb<Codepoint> =
   Arb.of(listOf(
      9,  // TAB
      10, // LINE FEED
      11, // LINE TABULATION
      12, // FORM FEED
      13, // CARRIAGE RETURN
      32, // SPACE
   ).map(::Codepoint))

data class Codepoint(val value: Int) {
   companion object
}

val Codepoint.isBmpCodePoint: Boolean
   get() = value ushr 16 == 0

val Codepoint.highSurrogate: Char
   get() = (value ushr 10).toChar() + (Char.MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10)).code

val Codepoint.lowSurrogate: Char
   get() = (value ushr 0x3ff).toChar() + Char.MIN_LOW_SURROGATE.code

fun Codepoint.asString(): String {
   return if (isBmpCodePoint) {
      value.toChar().toString()
   } else {
      charArrayOf(
         highSurrogate,
         lowSurrogate
      ).concatToString()
   }
}
