package io.kotest.datatest.styles

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.datatest.assertDataTestResults
import io.kotest.datatest.registerContextTests
import io.kotest.datatest.registerRootTests
import io.kotest.datatest.withData
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
         count shouldBe 201
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

      withData(nameFn = { "$it" }, 100, 101) { a ->
         Given("k") {
            When("l") {
               Then("m") {
                  a shouldBe a
               }
            }
         }
      }

      Given("d") {
         withData(nameFn = { "$it" }, 200, 201) { a ->
            When("e") {
               Then("f") {
                  a shouldBe a
               }
            }
         }
      }

      withData(nameFn = { "$it" }, 300, 301) { a ->
         Context("n") {
            Given("o") {
               When("p") {
                  Then("q") {
                     a shouldBe a
                  }
               }
            }
         }
      }
   }
}
