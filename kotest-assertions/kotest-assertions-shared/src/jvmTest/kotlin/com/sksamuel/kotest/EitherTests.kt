package com.sksamuel.kotest

import io.kotest.assertions.either
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs

class EitherTests : FunSpec({
   test("either fails if less than two assertions are executed") {
      shouldFail { either { "a" shouldBe "a" } }
   }

   test("either returns the original value if a single assertion succeeds") {
      either("foo") {
         this shouldBe "foo"
         this shouldBe "bar"
         this shouldBe "baz"
      }
   }

   test("either fails if more than one assertion succeed") {
      shouldFail {
         either("foo") {
            this shouldBe "foo"
            this shouldBe "foo"
            this shouldBe "baz"
         }
      }.message shouldContain "Either expected a single assertion to succeed but more than one succeeded"
   }

   test("either fails if all assertions fail") {
      shouldFail {
         either("cat") {
            this shouldBe "dog"
            this shouldBe "moose"
            this shouldBe "duck"
         }
      }.message shouldContain "Either expected a single assertion to succeed but they all failed"
   }

   test("either properly replaces original errors and assertion counts") {

   }
})
