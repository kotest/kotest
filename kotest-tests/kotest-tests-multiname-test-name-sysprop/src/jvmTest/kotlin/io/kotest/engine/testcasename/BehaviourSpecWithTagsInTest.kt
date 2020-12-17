package io.kotest.engine.testcasename

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BehaviourSpecWithTagsInTest : BehaviorSpec({
   given("a dummy given") {
      `when`("a dummy when") {
         then("a dummy then") {
            this.testCase.displayName shouldBe "Then: a dummy then"
         }
      }
   }
   given("a dummy given 2") {
      `when`("a dummy when  2") {
         then("a dummy then 2 with tags").config(tags = setOf(Dummy, NoUse)) {
            this.testCase.displayName shouldBe "Then: a dummy then 2 with tags[tags = Dummy, NoUse]"
         }
      }
   }
})
