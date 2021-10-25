package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class FunSpecForAllDataTest : FunSpec() {
   init {

      duplicateTestNameMode = DuplicateTestNameMode.Silent

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
