package io.kotest.datatest

import io.kotest.core.spec.style.FeatureSpec

internal class FeatureSpecForAllDataTest : FeatureSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      feature("inside a feature") {
         registerContextTests().assertDataTestResults()
         feature("inside another feature") {
            registerContextTests().assertDataTestResults()
            scenario("inside a scenario") {
               registerContextTests().assertDataTestResults()
            }
         }
      }
   }
}
