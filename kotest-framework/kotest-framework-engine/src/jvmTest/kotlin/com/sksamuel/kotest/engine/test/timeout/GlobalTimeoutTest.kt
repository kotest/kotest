package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days

class GlobalTimeoutTest : FunSpec() {
   init {
      test("global timeouts should apply if no other timeout is set") {
         val c = ProjectConfiguration().apply { timeout = 3 }
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(TestTimeouts::class)
            .withConfiguration(c)
            .launch()

         collector.waitForEnginesFinished()

         collector.names.shouldContainExactly("blocked", "suspend")

         collector.result("blocked").asClue { result -> result?.isError shouldBe true }
         collector.result("suspend").asClue { result -> result?.isError shouldBe true }
      }
   }
}

private class TestTimeouts : StringSpec() {
   init {
      // Long delays, to ensure tests will be interrupted. We'd notice a test that runs for a month.
      "blocked".config(blockingTest = true) { Thread.sleep(28.days.inWholeMilliseconds) }
      "suspend" { delay(28.days) }
   }
}
