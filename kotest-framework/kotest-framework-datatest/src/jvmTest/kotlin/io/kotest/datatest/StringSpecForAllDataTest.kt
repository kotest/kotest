package io.kotest.datatest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@ExperimentalKotest
internal class StringSpecForAllDataTest : StringSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 35
      }

      afterTest {
         count++
      }

      "inside a context" {
         shouldThrowAny {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
