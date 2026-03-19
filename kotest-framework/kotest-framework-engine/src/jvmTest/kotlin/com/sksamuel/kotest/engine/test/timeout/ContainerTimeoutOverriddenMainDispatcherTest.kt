package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

// Issue: https://github.com/kotest/kotest/issues/3703
@Isolate
@OptIn(ExperimentalCoroutinesApi::class)
class ContainerTimeoutOverriddenMainDispatcherTest : FunSpec({

   coroutineTestScope = true

   val dispatcher = StandardTestDispatcher()

   beforeSpec { Dispatchers.setMain(dispatcher) }
   afterSpec { Dispatchers.resetMain() }

   context("container with timeout").config(timeout = 100000.milliseconds) {
      test("test with delay") {
         // flips this test to use the dispatcher we installed on Dispatchers.Main earlier
         withContext(Dispatchers.Main) {
            // since we have a test dispatcher, a delay here should be instantly advanced,
            // and the Kotest timeout (which is wall time) shouldn't be hit
            delay(100.hours)
         }

         dispatcher.scheduler shouldBe testCoroutineScheduler
      }

      test("test exceeding invocation timeout").config(
         invocationTimeout = 1000.milliseconds,
         extensions = listOf(ExpectFailureExtension),
      ) {
         // since this is not a virtual time timeout, the Kotest wall clock timeout should be hit
         realTimeDelay(1.minutes)
      }
   }
})
