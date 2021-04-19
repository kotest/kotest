package io.kotest.datatest

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec

internal class StringSpecForAllDataTest : StringSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      "inside a context" {
         shouldThrowAny {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
