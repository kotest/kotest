package io.kotest.datatest.styles

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.assertDataTestResults
import io.kotest.datatest.registerContextTests
import io.kotest.datatest.registerRootTests
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
         count shouldBe 104
      }

      context("inside a context") {
         registerContextTests().assertDataTestResults()
         context("inside another context") {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
