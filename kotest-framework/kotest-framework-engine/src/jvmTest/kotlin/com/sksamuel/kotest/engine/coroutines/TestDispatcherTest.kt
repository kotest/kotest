package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherTest : FunSpec() {
   init {
      test("a test with TestDispatcher should advance time virtually").config(testCoroutineDispatcher = true) {
         val currentTime1 = testCoroutineScheduler.currentTime
         currentTime1 shouldBe 0L
         testCoroutineScheduler.advanceTimeBy(1234)
         val currentTime2 = testCoroutineScheduler.currentTime
         currentTime2 shouldBe 1234
      }
   }
}
