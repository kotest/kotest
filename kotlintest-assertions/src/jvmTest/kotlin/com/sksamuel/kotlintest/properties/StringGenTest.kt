package com.sksamuel.kotlintest.properties

import io.kotlintest.inspectors.forAll
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.matchers.string.shouldHaveMaxLength
import io.kotlintest.matchers.string.shouldHaveMinLength
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.string
import io.kotlintest.specs.FunSpec

class StringGenTest : FunSpec({

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
