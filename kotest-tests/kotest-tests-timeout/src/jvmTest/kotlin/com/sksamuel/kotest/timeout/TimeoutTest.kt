package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.toDuration

@ExperimentalTime
class TimeoutTest : StringSpec() {

   init {

      extension(expectFailureExtension)

      "a testcase timeout should interrupt a blocked thread".config(timeout = 50.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      "a testcase timeout should interrupt a suspend function".config(timeout = 50.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      "a testcase timeout should interupt a nested coroutine".config(timeout = 50.milliseconds) {
         launch {
            // a  high value to ensure its interrupted, we'd notice a test that runs for ever
            delay(10.toDuration(TimeUnit.HOURS))
         }
      }

      "a testcase timeout should interrupt suspended coroutine scope".config(timeout = 50.milliseconds) {
         someCoroutine()
      }

      "a testcase timeout should apply to all invocations".config(timeout = 200.milliseconds, invocations = 3) {
         delay(100)
      }

      "an invocation timeout should apply to individual test only".config(
         invocationTimeout = 100.milliseconds,
         timeout = 1000.milliseconds
      ) {
         delay(200)
      }
   }
}

suspend fun someCoroutine() {
   coroutineScope {
      launch {
         delay(10000000)
      }
   }
}
