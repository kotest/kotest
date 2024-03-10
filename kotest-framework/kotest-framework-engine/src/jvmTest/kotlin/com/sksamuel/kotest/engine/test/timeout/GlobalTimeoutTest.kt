package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

class GlobalTimeoutTest : FunSpec() {
   init {
      test("global timeouts should apply if no other timeout is set") {
         val c = ProjectConfiguration().apply { timeout = 3 }
         c.includePrivateClasses = true

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(TestTimeouts::class)
            .withConfiguration(c)
            .launch()
         collector.tests.mapKeys { it.key.name.testName }["blocked"]?.isError shouldBe true
         collector.tests.mapKeys { it.key.name.testName }["suspend"]?.isError shouldBe true
      }
   }
}

private class TestTimeouts : StringSpec() {
   init {
      "blocked".config(blockingTest = true) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      "suspend" {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }
   }
}
