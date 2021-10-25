package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
internal class ExpectSpecForAllDataTest : ExpectSpec() {
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

      context("inside a context") {
         registerContextTests().assertDataTestResults()
         context("inside another context") {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
