package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * The minimum value of a Unicode code point, constant {@code U+000000}.
 * http://www.unicode.org/glossary/#code_point
 */
internal const val MIN_CODE_POINT = 0x000000

/**
 * The maximum value of a Unicode code point, constant {@code U+10FFFF}.
 * http://www.unicode.org/glossary/#code_point
 */
internal const val MAX_CODE_POINT = 0X10FFFF

internal const val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000

fun Arb.Companion.codepoints(): Arb<Codepoint> =
   Arb.int(MIN_CODE_POINT..MAX_CODE_POINT)
      .withEdgecases(MIN_CODE_POINT, MAX_CODE_POINT)
      .map { Codepoint(it) }

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.az()")
)
fun Arb.Companion.az() = Codepoint.az()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.alphanumeric()")
)
fun Arb.Companion.alphanumeric(): Arb<Codepoint> =
   Codepoint.alphanumeric()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.ascii()")
)
fun Arb.Companion.ascii(): Arb<Codepoint> =
   Codepoint.ascii()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.georgian()")
)
fun Arb.Companion.georgian(): Arb<Codepoint> =
   Codepoint.georgian()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.katakana()")
)
fun Arb.Companion.katakana(): Arb<Codepoint> =
   Codepoint.katakana()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.greekCoptic()")
)
fun Arb.Companion.greekCoptic(): Arb<Codepoint> =
   Codepoint.greekCoptic()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.armenian()")
)
fun Arb.Companion.armenian(): Arb<Codepoint> =
   Codepoint.armenian()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.hebrew()")
)
fun Arb.Companion.hebrew(): Arb<Codepoint> =
   Codepoint.hebrew()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.arabic()")
)
fun Arb.Companion.arabic(): Arb<Codepoint> =
   Codepoint.arabic()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.cyrillic()")
)
fun Arb.Companion.cyrillic(): Arb<Codepoint> =
   Codepoint.cyrillic()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.hiragana()")
)
fun Arb.Companion.hiragana(): Arb<Codepoint> =
   Codepoint.hiragana()

@Deprecated("Codepoints in Arb.{code point} are deprecated. Use Codepoint.{code point} instead.",
   ReplaceWith("Codepoint.egyptianHieroglyphs()")
)
fun Arb.Companion.egyptianHieroglyphs(): Arb<Codepoint> =
   Codepoint.egyptianHieroglyphs()

fun Codepoint.Companion.az(): Arb<Codepoint> =
   Arb.int('a'.code..'z'.code)
      .map { Codepoint(it) }
      .withEdgecases(Codepoint('a'.code))

fun Codepoint.Companion.alphanumeric(): Arb<Codepoint> =
   Arb.element((('a'..'z') + ('A'..'Z') + ('0'..'9')).toList()).map { Codepoint(it.code) }

fun Codepoint.Companion.ascii(): Arb<Codepoint> =
   Arb.int(' '.code..'z'.code)
      .map { Codepoint(it) }
      .withEdgecases(Codepoint('a'.code))

fun Codepoint.Companion.georgian(): Arb<Codepoint> {
   val empty = listOf(0x10C6, 0x10ce, 0x10cf) + (0x10c8..0x10cC).toList()
   return Arb.int(0x10A0..0x10FF)
      .withEdgecases(0x10A0)
      .filterNot { it in empty }
      .map { Codepoint(it) }
}

fun Codepoint.Companion.katakana(): Arb<Codepoint> =
   Arb.int(0x30A0..0x30FF)
      .withEdgecases(0x30A1)
      .map { Codepoint(it) }

fun Codepoint.Companion.greekCoptic(): Arb<Codepoint> {
   val empty = (0x0380..0x0383).toList() + listOf(0x0378, 0x0379, 0x038B, 0x038D, 0x03A2)
   return Arb.int(0x0370..0x03FF)
      .withEdgecases(0x03B1)
      .filterNot { it in empty }
      .map { Codepoint(it) }
}

fun Codepoint.Companion.armenian(): Arb<Codepoint> {
   val empty = listOf(0x0557, 0x0558, 0x058B, 0x058C)
   return Arb.int(0x0531..0x058F)
      .withEdgecases(0x0531)
      .filterNot { it in empty }
      .map { Codepoint(it) }
}

fun Codepoint.Companion.hebrew(): Arb<Codepoint> {
   val empty = (0x05c8..0x05cF).toList() + (0x05eB..0x05eE).toList()
   return Arb.int(0x0591..0x05F4)
      .withEdgecases(0x05D0)
      .filterNot { it in empty }
      .map { Codepoint(it) }
}

fun Codepoint.Companion.arabic(): Arb<Codepoint> {
   val empty = listOf(0x062D)
   return Arb.int(0x0600..0x06FF)
      .withEdgecases(0x0627)
      .filterNot { it in empty }
      .map { Codepoint(it) }
}

fun Codepoint.Companion.cyrillic(): Arb<Codepoint> =
   Arb.int(0x0400..0x04FF).withEdgecases(0x0430).map { Codepoint(it) }

fun Codepoint.Companion.hiragana(): Arb<Codepoint> {
   val empty = listOf(0x3097, 0x3098)
   return Arb.int(0x3041..0x309F)
      .withEdgecases(0x3041)
      .filterNot { it in empty }
      .map { Codepoint(it) }
}

fun Codepoint.Companion.egyptianHieroglyphs(): Arb<Codepoint> =
   Arb.int(0x13000..0x1342E).withEdgecases(0x13000).map { Codepoint(it) }

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
