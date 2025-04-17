package io.kotest.property

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MutableConfigTest {
   @Test
   fun canMutatePropTestConfigValues() {
      PropertyTesting.defaultIterationCount = 1234
      PropertyTesting.defaultIterationCount shouldBe 1234
      PropertyTesting.defaultIterationCount = 4
      PropertyTesting.defaultIterationCount shouldBe 4
   }
}
