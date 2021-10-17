package io.kotest.datatest

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
internal class WordSpecForAllDataTest : WordSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 34
      }
   }
}
