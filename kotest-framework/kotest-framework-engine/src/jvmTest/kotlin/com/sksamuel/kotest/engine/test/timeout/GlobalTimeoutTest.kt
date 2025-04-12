package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class GlobalTimeoutTest : FunSpec() {
   init {
      context("global timeouts should apply if no other timeout is set") {
         withData(
            nameFn = { "coroutineTestScope = $it" },
            first = false,
            second = true,
         ) { enableCoroutineTestScope ->

            val c = object : AbstractProjectConfig() {
               override val timeout = 100.milliseconds
               override val coroutineTestScope = enableCoroutineTestScope
            }
            val collector = CollectingTestEngineListener()

            TestEngineLauncher(collector)
               .withClasses(TestTimeouts::class)
               .withProjectConfig(c)
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
