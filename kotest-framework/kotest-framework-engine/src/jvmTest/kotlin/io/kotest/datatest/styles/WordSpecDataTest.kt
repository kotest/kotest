package io.kotest.datatest.styles

import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.assertDataTestResults
import io.kotest.datatest.registerRootTests
import io.kotest.matchers.shouldBe

class WordSpecDataTest : WordSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 36
      }
   }
}
