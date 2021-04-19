package io.kotest.datatest

import io.kotest.core.spec.style.FreeSpec

class FreeSpecForAllDataClassDataTest : FreeSpec() {
   init {

      val results = registerRootTests()

      afterSpec {
         results.assertDataTestResults()
      }

      "inside a context" - {
         registerContextTests().assertDataTestResults()
         "inside another context" - {
            registerContextTests().assertDataTestResults()
         }
      }
   }
}
