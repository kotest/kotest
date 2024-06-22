package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.spec.style.FreeSpec

@Suppress("BlockingMethodInNonBlockingContext")
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

     timeout = 400

     "Test 1" {
        Thread.sleep(100)
     }

     "Test 2" {
        Thread.sleep(100)
     }

     "Test 3" {
        Thread.sleep(100)
     }

     "Test 4" {
        Thread.sleep(100)
     }

     "Test 5" {
        Thread.sleep(100)
     }

     "Test 6" {
        Thread.sleep(100)
     }

     "Test 7" {
        Thread.sleep(100)
     }
  }
}
