package com.sksamuel.kotest.engine.extensions.spec.beforespec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.seconds

@Suppress("RunBlockingInSuspendFunction")
@OptIn(ExperimentalAtomicApi::class)
class BeforeSpecConcurrencyTest : FreeSpec() {

   private val beforeSpecCompleted = AtomicBoolean(false)

   override suspend fun beforeSpec(spec: Spec) {
      runBlocking {
         repeat(3) {
            async {
               delay(1.seconds)
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
