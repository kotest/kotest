package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
internal class DescribeSpecForAllDataTest : DescribeSpec() {
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
