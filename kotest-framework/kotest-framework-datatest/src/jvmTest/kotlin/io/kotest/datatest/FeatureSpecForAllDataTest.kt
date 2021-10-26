package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
internal class FeatureSpecForAllDataTest : FeatureSpec() {
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

      feature("inside a feature") {
         registerContextTests().assertDataTestResults()
         feature("inside another feature") {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
