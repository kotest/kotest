package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec

@ExperimentalKotest
internal class FunSpecForAllDataTest : FunSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      test("inside a test case") {
         registerContextTests().assertDataTestResults()
      }

      context("inside a context") {
         registerContextTests().assertDataTestResults()
         context("inside another context") {
            registerContextTests().assertDataTestResults()
            test("inside a contexted test case") {
               registerContextTests().assertDataTestResults()
            }
         }
      }
   }
}
