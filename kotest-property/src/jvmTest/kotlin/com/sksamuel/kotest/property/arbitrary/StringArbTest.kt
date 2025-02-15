package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arabic
import io.kotest.property.arbitrary.armenian
import io.kotest.property.arbitrary.asString
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.cyrillic
import io.kotest.property.arbitrary.georgian
import io.kotest.property.arbitrary.greekCoptic
import io.kotest.property.arbitrary.hebrew
import io.kotest.property.arbitrary.hiragana
import io.kotest.property.arbitrary.katakana
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.printableAscii
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.whitespace
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlin.streams.toList

@EnabledIf(LinuxCondition::class)
class StringArbTest : FunSpec() {

   init {

      fun String.codepoints() = codePoints().toList()

      test("String arbitraries") {
         forAll(
            Arb.string(0, 10),
            Arb.string(0, 5)
         ) { a, b ->
            (a + b).length == a.length + b.length
         }
      }

      test("should honour sizes") {
         forAll(Arb.string(10..20)) {
            it.length >= 10
            it.length <= 20
         }
         forAll(Arb.string(3..8)) {
            it.length >= 3
            it.length <= 8
         }
         forAll(Arb.string(0..10)) { it.length <= 10 }
         forAll(Arb.string(0..3)) { it.length <= 3 }
         forAll(Arb.string(4..4)) { it.length == 4 }
         forAll(Arb.string(1..3)) {
            it.isNotEmpty()
            it.length <= 3
         }
      }

      test("all ascii strings generated should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.ascii())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all printable ascii strings generated should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.printableAscii())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all alphanumeric strings generated should be valid codepoints") {
         checkAll(Arb.string(10..20, Codepoint.alphanumeric())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("alphanumeric strings should generate all possible strings") {
         val chars = mutableSetOf<Char>()
         Codepoint.alphanumeric().map { it.asString().single() }.checkAll {
            chars += it
         }
         chars shouldContainExactlyInAnyOrder (('a'..'z') + ('A'..'Z') + ('0'..'9')).toList()
      }

      test("all strings generated with georgian codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.georgian())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with Katakana codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.katakana())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with hiragana codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.hiragana())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with greek coptic Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.greekCoptic())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with armenian codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.armenian())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with hebrew Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.hebrew())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with arabic codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.arabic())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with cyrillic Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.cyrillic())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with whitespace codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.whitespace())) { a ->
            a.codepoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with whitespace codepoints should be whitespace only") {
         checkAll(Arb.string(10..20, Codepoint.whitespace())) { a ->
            a.codepoints().forEach {
               Character.isWhitespace(it)
            }
         }
      }

      test("all strings generated with list of acceptable characters should only contain those characters") {
         val chars = listOf("a", "z", "3", "?", "-")
         checkAll(Arb.string(10..20, chars.joinToString(""))) { a ->
            a.toList().forEach {
               it.toString() in chars
            }
         }
      }
   }
}
