package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

class TestCoroutineSchedulerTest : FunSpec() {
   init {
      test("delay controller should control time").config(testCoroutineDispatcher = true) {
         val duration = 1.days
         launch {
            delay(duration)
         }
         // if this isn't working, the above test will just take forever
         testCoroutineScheduler.advanceTimeBy(duration)
      }
   }
}
