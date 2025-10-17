package com.sksamuel.kotest.engine.concurrency

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.milliseconds

@Suppress("RunBlockingInSuspendFunction")
@OptIn(ExperimentalAtomicApi::class)
class BeforeSpecConcurrencyTest : StringSpec() {

   val beforeSpecCompleted = AtomicBoolean(false)

   override suspend fun beforeSpec(spec: Spec) {
      runBlocking {
         repeat(3) {
            async {
               delay(500.milliseconds)
            }
         }
      }
      beforeSpecCompleted.exchange(true)
   }

   init {

      testExecutionMode = TestExecutionMode.Concurrent

      "all tests should wait for beforeSpec to complete when running with concurrency 1" {
         beforeSpecCompleted.load().shouldBeTrue()
      }

      "all tests should wait for beforeSpec to complete when running with concurrency 2" {
         beforeSpecCompleted.load().shouldBeTrue()
      }

      "all tests should wait for beforeSpec to complete when running with concurrency 3" {
         beforeSpecCompleted.load().shouldBeTrue()
      }
   }


}
