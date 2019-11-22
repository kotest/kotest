package com.sksamuel.kotest.properties

import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldHaveMaxLength
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.string
import io.kotest.specs.FunSpec

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
})
