package io.kotest.datatest

import io.kotest.core.spec.style.ExpectSpec

internal class ExpectSpecForAllDataTest : ExpectSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      expect("inside an expect") {
         registerContextTests().assertDataTestResults()
      }

      context("inside a context") {
         registerContextTests().assertDataTestResults()
         context("inside another context") {
            registerContextTests().assertDataTestResults()
            expect("inside a contexted expect") {
               registerContextTests().assertDataTestResults()
            }
         }
      }
   }
}
