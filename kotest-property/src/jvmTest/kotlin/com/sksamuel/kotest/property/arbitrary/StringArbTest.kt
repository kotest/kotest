package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arabic
import io.kotest.property.arbitrary.armenian
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.cyrillic
import io.kotest.property.arbitrary.georgian
import io.kotest.property.arbitrary.greekCoptic
import io.kotest.property.arbitrary.hebrew
import io.kotest.property.arbitrary.hiragana
import io.kotest.property.arbitrary.katakana
import io.kotest.property.arbitrary.string
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
         checkAll(Arb.string(10..20, Arb.ascii())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with georgian codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.georgian())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with Katakana codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.katakana())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with hiragana codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.hiragana())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with greek coptic Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.greekCoptic())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with armenian codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.armenian())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with hebrew Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.hebrew())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with arabic codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.arabic())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }

      test("all strings generated with cyrillic Codepoints should be valid code codepoints") {
         checkAll(Arb.string(10..20, Arb.cyrillic())) { a ->
            a.codePoints().forEach {
               Character.isValidCodePoint(it)
            }
         }
      }
   }
}
