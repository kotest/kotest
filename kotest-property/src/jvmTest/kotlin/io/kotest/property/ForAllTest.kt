package io.kotest.property

import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class ForAllTest : FunSpec({

   test("forAll with 2 arbitraries") {

      val context = forAll(
         Arbitrary.int(1000),
         Arbitrary.int(1000)
      ) { a, b -> a + b == b + a }

      context.attempts() shouldBe 2000
      context.successes() shouldBe 2000
      context.failures() shouldBe 1000
   }

   test("forAll with 2 progressions") {

   }
})
