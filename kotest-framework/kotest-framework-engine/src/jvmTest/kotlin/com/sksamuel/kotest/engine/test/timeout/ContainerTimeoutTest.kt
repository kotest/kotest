package com.sksamuel.kotest.engine.test.timeout

import io.kotest.assertions.asClue
import io.kotest.common.testTimeSource
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

@EnabledIf(LinuxCondition::class)
class ContainerTimeoutTest : FunSpec() {
   init {
      coroutineTestScope = true

      test("container test should timeout if nested exceeds parent timeout") {
         val collector = CollectingTestEngineListener()

         val duration = testTimeSource().measureTime {
            TestEngineLauncher(collector)
               .withClasses(NestedTimeout::class)
               .async()
         }

         duration shouldBe 100.milliseconds

         collector.names.shouldContainExactly("a")

         collector.result("a").asClue { result ->
            result.shouldNotBeNull()
            result.isError shouldBe true
            result.errorOrNull?.message shouldBe "Test 'a' did not complete within 100ms"
         }
      }
   }
}

private class NestedTimeout : FunSpec() {
   init {
      context("a").config(timeout = 100.milliseconds) {
         test("b") {
            delay(2000.milliseconds)
         }
      }
   }
}
