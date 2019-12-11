package com.sksamuel.kotest.properties

import io.kotest.data.forall
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveMaxLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.char
import io.kotest.properties.string
import io.kotest.specs.FunSpec
import io.kotest.tables.row

class GenStringTest : FunSpec({

   test("should honour min size") {
      assertAll(Gen.string(minSize = 10)) { it.shouldHaveMinLength(10) }
      assertAll(Gen.string(minSize = 3)) { it.shouldHaveMinLength(3) }
      assertAll(Gen.string(minSize = 1)) { it.shouldHaveMinLength(1) }
   }

   test("should honour max size") {
      assertAll(Gen.string(maxSize = 10)) { it.shouldHaveMaxLength(10) }
      assertAll(Gen.string(maxSize = 3)) { it.shouldHaveMaxLength(3) }
      assertAll(Gen.string(maxSize = 1)) { it.shouldHaveMaxLength(1) }
   }

   test("should honour min and max size") {
      assertAll(Gen.string(minSize = 10, maxSize = 10)) { it.shouldHaveLength(10) }
      assertAll(Gen.string(minSize = 1, maxSize = 3)) {
         it.shouldHaveMinLength(1)
         it.shouldHaveMaxLength(3)
      }
      assertAll(Gen.string(minSize = 1, maxSize = 1)) { it.shouldHaveLength(1) }
   }

   test("should produce non-whitespace ASCII characters when no Gen<Char> provided") {
      assertAll(10000, Gen.string(minSize = 1, maxSize = 100)) { string ->
         string.forEach { char -> char.toInt() shouldBeInRange (33..126) }
      }
   }

   test("should produce only characters from provided Gen<Char>") {
      val cyrillicRange = '\u0400'..'\u04FF'
      val thaiRange     = '\u0E01'..'\u0E2F'
      val runicRange    = '\u16A0'..'\u16F0'
      forall(
         row(cyrillicRange),
         row(thaiRange),
         row(runicRange)
      ) { charRange ->
         val genString = Gen.string(minSize = 1, maxSize = 100, genChar = Gen.char(charRange))
         assertAll(500, genString) { string ->
            string.forEach { char -> (char in charRange).shouldBeTrue() }
         }
      }
   }
})
