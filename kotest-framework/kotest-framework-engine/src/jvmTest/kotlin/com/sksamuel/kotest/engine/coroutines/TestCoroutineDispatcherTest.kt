package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.delayController
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

@ExperimentalStdlibApi
@ExperimentalTime
class TestCoroutineDispatcherTest : FunSpec() {
   init {
      test("a test with TestCoroutineDispatcher should advance time virtually").config(testCoroutineDispatcher = true) {
         val currentTime1 = delayController.currentTime
         currentTime1 shouldBe 0L
         delayController.advanceTimeBy(1234)
         val currentTime2 = delayController.currentTime
         currentTime2 shouldBe 1234
      }
   }
}
