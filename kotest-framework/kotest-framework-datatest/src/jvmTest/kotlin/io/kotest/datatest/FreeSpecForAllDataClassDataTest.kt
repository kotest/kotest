package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
class FreeSpecForAllDataClassDataTest : FreeSpec() {
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

      "inside a context" - {
         registerContextTests().assertDataTestResults()
         "inside another context" - {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
