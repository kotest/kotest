package com.sksamuel.kotest.specs.shouldspec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ShouldSpecLambdaTest : ShouldSpec({

  var name: String? = null

   context("feature 1") {
      should("the name should start off null") {
         name.shouldBe(null)
      }
      name = "foo"
      context("now the name should be set to foo") {
         name.shouldBe("foo")
         should("should still be foo for this nested test") {
            name.shouldBe("foo")
         }
         name = "boo"
         should("now the name should be boo") {
            name.shouldBe("boo")
         }
      }
      context("it should still be boo as this test should run after all the above") {
         name.shouldBe("boo")
      }
    name = "koo"
      context("now the name should be set to koo") {
         name.shouldBe("koo")
      }
  }

   context("feature 2 should run after feature 1") {
      should("name should still be the last value which was koo") {
         name shouldBe "koo"
      }
   }
})
