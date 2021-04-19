package io.kotest.datatest

import io.kotest.core.spec.style.DescribeSpec

internal class DescribeSpecForAllDataTest : DescribeSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      it("inside an it") {
         registerContextTests().assertDataTestResults()
      }

      describe("inside a describe") {
         registerContextTests().assertDataTestResults()
         describe("inside another describe") {
            registerContextTests().assertDataTestResults()
            it("inside a contexted it") {
               registerContextTests().assertDataTestResults()
            }
         }
      }
   }
}
