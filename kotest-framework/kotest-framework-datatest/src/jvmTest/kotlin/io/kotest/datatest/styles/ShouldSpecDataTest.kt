package io.kotest.datatest.styles

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.assertDataTestResults
import io.kotest.datatest.registerContextTests
import io.kotest.datatest.registerRootTests
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class ShouldSpecDataTest : ShouldSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 108
      }

      context("inside a context") {
         registerContextTests().assertDataTestResults()
         context("inside another context") {
            registerContextTests().assertDataTestResults()
         }
      }

      context("a context should allow nullable maps") {
         withData(
            mapOf(
               "true" to true,
               "false" to false,
               "null" to null,
            )
         ) { _ ->
         }
      }
   }
}
