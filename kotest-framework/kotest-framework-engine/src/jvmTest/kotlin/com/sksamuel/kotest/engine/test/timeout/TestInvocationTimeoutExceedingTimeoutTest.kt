package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Tests that an `invocationTimeout` cannot exceed test case `timeout`.
 */
class TestInvocationTimeoutExceedingTimeoutTest : FunSpec() {
   init {
      test("invocation timeout shouldn't exceed test timeout") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(SpecWithInvalidInvocationTimeout::class)
            .launch()

         collector.specs.getValue(SpecWithInvalidInvocationTimeout::class).errorOrNull
            .shouldBeInstanceOf<IllegalStateException>()
            .shouldHaveMessage("The invocationTimeout cannot exceed the test case timeout: 10s > 1s")
      }
   }
}

private class SpecWithInvalidInvocationTimeout : FunSpec() {
   init {
      test("invalid timeout").config(
         invocationTimeout = 10.seconds,
         timeout = 1.seconds
      ) {
         delay(1)
      }
   }
}
