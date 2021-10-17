package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
internal class BehaviorSpecDataTest : BehaviorSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 209
      }

      given("inside a given") {
         registerContextTests().assertDataTestResults()
         and("inside an and") {
            registerContextTests().assertDataTestResults()
         }
         When("inside a when") {
            registerContextTests().assertDataTestResults()
            and("inside an and") {
               registerContextTests().assertDataTestResults()
            }
            then("inside a then") {
               registerContextTests().assertDataTestResults()
            }
         }
      }
   }
}
