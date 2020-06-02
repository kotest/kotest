package com.sksamuel.kotest.specs.feature

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class FeatureSpecLambdaTest : FeatureSpec({

   var name: String? = null

   feature("feature 1") {
      scenario("the name should start off null") {
         name.shouldBe(null)
      }
      name = "foo"
      feature("now the name should be set to foo") {
         name.shouldBe("foo")
         scenario("should still be foo for this nested test") {
            name.shouldBe("foo")
         }
         name = "boo"
         scenario("now the name should be boo") {
            name.shouldBe("boo")
         }
      }
      scenario("it should still be boo as this test should run after all the above") {
         name.shouldBe("boo")
      }
      name = "koo"
      scenario("now the name should be set to koo") {
         name.shouldBe("koo")
      }
   }

   feature("feature 2 should run after feature 1") {
      scenario("name should still be the last value which was koo") {
         name shouldBe "koo"
      }
   }
})
