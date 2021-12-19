package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.delayController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.days

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
class DelayControllerTest : FunSpec() {
   init {
      test("delay controller should control time").config(testCoroutineDispatcher = true) {
         val duration = 1.days
         launch {
            delay(duration.inWholeMilliseconds)
         }
         // if this isn't working, the above test will just take forever
         delayController.advanceTimeBy(duration.inWholeMilliseconds)
      }
   }
}
