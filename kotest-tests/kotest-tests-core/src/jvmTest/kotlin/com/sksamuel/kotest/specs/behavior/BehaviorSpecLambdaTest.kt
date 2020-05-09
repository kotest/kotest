package com.sksamuel.kotest.specs.behavior

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviorSpecLambdaTest : BehaviorSpec({

   var name: String? = null

   given("null name") {
      When("we have not yet initialized the name") {
         Then("the name should be null") {
            name.shouldBe(null)
         }
      }
      name = "foo"
      When("the name has been set to foo before this block") {
         name.shouldBe("foo")
         Then("should be foo") {
            name.shouldBe("foo")
         }
         name = "boo"
         Then("should be boo") {
            name.shouldBe("boo")
         }
      }
      and("we continue a when clause") {
         name.shouldBe("boo")
         Then("name should still be boo") {
            name.shouldBe("boo")
         }
      }
      name = "koo"
      When("now the name should be set to koo") {
         name.shouldBe("koo")
         Then("it should still be koo in this scope") {
            name.shouldBe("koo")
         }
      }
   }

   given("the second given block") {
      When("the first block has completed") {
         Then("name should still be the last value which was koo") {
            name shouldBe "koo"
         }
      }
   }
})
