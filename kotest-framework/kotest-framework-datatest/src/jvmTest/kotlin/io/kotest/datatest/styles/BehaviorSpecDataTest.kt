package io.kotest.datatest.styles

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.assertDataTestResults
import io.kotest.datatest.registerContextTests
import io.kotest.datatest.registerRootTests
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class BehaviorSpecDataTest : BehaviorSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 174
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
         }
      }
   }
}
