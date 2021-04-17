package com.sksamuel.kotest

import io.kotest.assertions.all
import io.kotest.assertions.any
import io.kotest.assertions.assertionCounter
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class AnyTests : FunSpec({
   test("should not fail if no assertions are executed") {

      any("bar") {

      } shouldBe "bar"
   }

   test("should return the original value if a single assertion succeeds") {
      val before: Int = assertionCounter.get()
      val result = any("foo") {
         this shouldBe "foo"
         this shouldBe "bar"
      }

      before shouldBe assertionCounter.get() - 2
      result shouldBe "foo"
   }

   test("any expects at least one assertion to succeed") {
      val original = assertionCounter.get()

      val result = any {
         "a" shouldBe "a"
         "a" shouldBe "b"
         "b" shouldBe "b"
         "b" shouldBe "c"

         "foo"
      }

      assertionCounter.get() shouldBe original + 4
      result shouldBe "foo"
   }

   test("any fails if all assertions fail") {


      shouldFail {
         original = assertionCounter.get()

         any {
            "a" shouldBe "b"
            "b" shouldBe "c"
            "c" shouldBe "d"
            "d" shouldBe "e"
         }
      }

      val result = all {
         any {
            "a" shouldBe "b"
            "b" shouldBe "c"
            "c" shouldBe "d"
            "d" shouldBe "e"

            "foo"
         }
      }

      assertionCounter.get() shouldBe original + 4
      result.shouldBeNull()
   }


   test("all properly replaces original errors and assertion counts") {

   }
})
