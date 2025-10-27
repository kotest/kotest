package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class ContainerTimeoutTest : FunSpec() {
   init {
      context("container test should timeout if nested exceeds parent timeout") {
         withData(
            nameFn = { (coroutineTestScope, message) -> "coroutineTestScope = $coroutineTestScope -> $message" },
            false to "Test 'a' did not complete within 100ms",
            true to "After waiting for 100ms, the test body did not run to completion",
         ) { (enableCoroutineTestScope, message) ->
            val collector = CollectingTestEngineListener()
            val c = object : AbstractProjectConfig() {
               override val coroutineTestScope = enableCoroutineTestScope
            }
            TestEngineLauncher()
               .withListener(collector)
               .withProjectConfig(c)
               .withClasses(NestedTimeout::class)
               .launch()

            collector.names.shouldContainExactly("a")

            collector.result("a").asClue { result ->
               result.shouldNotBeNull()
               result.isErrorOrFailure shouldBe true
               result.errorOrNull?.message shouldStartWith message
            }
         }
      }
   }
}

private class NestedTimeout : FunSpec() {
   init {
      context("a").config(timeout = 100.milliseconds) {
         test("b") {
            realTimeDelay(2000.milliseconds)
         }
      }
   }
}
