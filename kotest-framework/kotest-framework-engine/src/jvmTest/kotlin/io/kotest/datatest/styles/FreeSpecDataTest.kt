package io.kotest.datatest.styles

import io.kotest.engine.datatest.withData
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.assertDataTestResults
import io.kotest.datatest.registerContextTests
import io.kotest.datatest.registerRootTests
import io.kotest.matchers.shouldBe

class FreeSpecDataTest : FreeSpec() {
   init {

      val results = registerRootTests()
      var count = 0

      afterTest {
         count++
      }

      afterSpec {
         results.assertDataTestResults()
         count shouldBe 110
      }

      "inside a context" - {
         registerContextTests().assertDataTestResults()
         "inside another context" - {
            registerContextTests().assertDataTestResults()
         }
      }

      "a context should allow nullable maps" - {
         withData(
            mapOf(
               "true" to true,
               "false" to false,
               "null" to null,
            )
         ) { _ ->
         }
      }
   }
}
