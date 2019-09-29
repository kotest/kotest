package com.sksamuel.kotest

import io.kotest.specs.FreeSpec
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@UseExperimental(ExperimentalTime::class)
@Suppress("BlockingMethodInNonBlockingContext")
class MultipleTestTimeoutTest : FreeSpec() {

  // The test executor was failing because as it reutilizes some threads from a thread pool.
  // When using that thread pool, a task to cancel the thread is created, so that the engine can interrupt
  // a test that is going forever.
  // However, if the task is not cancelled, it will eventually interrupt the thread when it's running another task
  // in the thread pool, interrupting a test that hasn't timed out yet, which is undesired.

  init {
    // 100 millis sleep will "accumulate" between tests. If the context is still shared, one of them will fail
    // due to timeout.
    "Test 1".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 2".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 3".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 4".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 5".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 6".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }

    "Test 7".config(timeout = 300.milliseconds) {
      Thread.sleep(100)
    }
  }
}
