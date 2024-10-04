package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FreeSpec

@Suppress("BlockingMethodInNonBlockingContext")
@EnabledIf(LinuxCondition::class)
class MultipleTestTimeoutTest : FreeSpec() {

   /*
    * The test executor was failing because as it reutilizes some threads from a thread pool.
    * When using that thread pool, a task to cancel the thread is created, so that the engine can interrupt
    * a test that is going forever.
    *  However, if the task is not cancelled, it will eventually interrupt the thread when it's running another task
    * in the thread pool, interrupting a test that hasn't timed out yet, which is undesired.
    */
   init {
      // 100 millis sleep will "accumulate" between tests. If the context is still shared,
      // one of them will fail due to the cumulative time exceeding the timeouts.

      timeout = 200

      "Test 1" {
         Thread.sleep(25)
      }

      "Test 2" {
         Thread.sleep(25)
      }

      "Test 3" {
         Thread.sleep(25)
      }

      "Test 4" {
         Thread.sleep(25)
      }

      "Test 5" {
         Thread.sleep(25)
      }

      "Test 6" {
         Thread.sleep(25)
      }

      "Test 7" {
         Thread.sleep(25)
      }

      "Test 8" {
         Thread.sleep(25)
      }

      "Test 9" {
         Thread.sleep(25)
      }

      "Test 10" {
         Thread.sleep(25)
      }
   }
}
