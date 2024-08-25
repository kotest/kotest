package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.days

class GlobalTimeoutTest : FunSpec() {
   init {
      context("global timeouts should apply if no other timeout is set") {
         withData(
            nameFn = { "coroutineTestScope = $it" },
            false,
            true,
         ) { enableCoroutineTestScope ->
            val c = ProjectConfiguration().apply {
               timeout = 100
               coroutineTestScope = enableCoroutineTestScope
            }
            val collector = CollectingTestEngineListener()

            TestEngineLauncher(collector)
               .withClasses(TestTimeouts::class)
               .withConfiguration(c)
               .launch()

            collector.names.shouldContainExactly("blocked", "suspend")

            collector.result("blocked").asClue { result -> result?.isErrorOrFailure shouldBe true }
            collector.result("suspend").asClue { result -> result?.isErrorOrFailure shouldBe true }
         }
      }
   }
}

private class TestTimeouts : StringSpec({
   // Long delays, to ensure tests will be interrupted. We'd notice a test that runs for a month.
   "blocked".config(blockingTest = true) {
      Thread.sleep(28.days.inWholeMilliseconds)
   }

   "suspend" {
      realTimeDelay(28.days)
   }
})
