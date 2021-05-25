package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.WordSpec

@ExperimentalKotest
internal class WordSpecForAllDataTest : WordSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      "inside a when" `when` {
         registerContextTests().assertDataTestResults()
         "inside a should" should {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
