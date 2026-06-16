package com.sksamuel.kotest.matchers.tuples

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.tuples.*

class TripleTest : FunSpec() {
   init {

      test("triple should have first") {
         Triple(1, 2, 3).shouldHaveFirst(1)
         Triple(1, 2, 3).shouldNotHaveFirst(2)
         shouldFail {
            Triple(1, 2, 3).shouldNotHaveFirst(1)
         }.message shouldBe "Triple (1, 2, 3) should not have first value 1"
      }

      test("triple should have second") {
         Triple(1, 2, 3).shouldHaveSecond(2)
         Triple(1, 2, 3).shouldNotHaveSecond(1)
         shouldFail {
            Triple(1, 2, 3).shouldNotHaveSecond(2)
         }.message shouldBe "Triple (1, 2, 3) should not have second value 2"
      }

      test("triple should have third") {
         Triple(1, 2, 3).shouldHaveThird(3)
         Triple(1, 2, 3).shouldNotHaveThird(2)
         shouldFail {
            Triple(1, 2, 3).shouldNotHaveThird(3)
         }.message shouldBe "Triple (1, 2, 3) should not have third value 3"
      }
   }
}
