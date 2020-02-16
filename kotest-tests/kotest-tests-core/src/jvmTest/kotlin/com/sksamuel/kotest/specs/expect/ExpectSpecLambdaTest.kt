package com.sksamuel.kotest.specs.expect

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class ExpectSpecLambdaTest : ExpectSpec({

   var name: String? = null

   context("context 1") {
      expect("the name should start off null") {
         name.shouldBe(null)
      }
      name = "foo"
      expect("the name should be foo in this test") {
         name.shouldBe("foo")
      }
      name = "boo"
      expect("now the name should be boo") {
         name.shouldBe("boo")
      }
      expect("it should still be boo as this test should run after all the above") {
         name.shouldBe("boo")
      }
      name = "koo"
      expect("now the name should be set to koo") {
         name.shouldBe("koo")
      }
   }

   context("context 2 should run after context 1") {
      expect("name should still be the last value which was koo") {
         name shouldBe "koo"
      }
   }
})
