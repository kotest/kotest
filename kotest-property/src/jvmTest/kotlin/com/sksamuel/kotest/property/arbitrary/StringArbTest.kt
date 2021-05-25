package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.forAll

class StringArbTest : FunSpec() {

   init {

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
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all alphanumeric strings generated should be valid codepoints") {
         checkAll(Arb.string(10..20, Codepoint.alphanumeric())) { a ->
            a.codePoints().forEach {
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
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with Katakana codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.katakana())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with hiragana codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.hiragana())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with greek coptic Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.greekCoptic())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with armenian codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.armenian())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with hebrew Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.hebrew())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with arabic codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.arabic())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with cyrillic Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Codepoint.cyrillic())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }
   }
}
