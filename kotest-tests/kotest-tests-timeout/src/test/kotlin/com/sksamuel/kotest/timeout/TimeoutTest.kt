package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration

class TimeoutTest : FunSpec() {

   init {

      extension(expectFailureExtension)

      test("a testcase timeout should interrupt a blocked thread").config(timeout = Duration.milliseconds(50)) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      test("a testcase timeout should interrupt a suspend function").config(timeout = Duration.milliseconds(50)) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      test("a testcase timeout should interupt a nested coroutine").config(timeout = Duration.milliseconds(50)) {
         launch {
            // a  high value to ensure its interrupted, we'd notice a test that runs for ever
            delay(Duration.hours(10))
         }
      }

      test("a testcase timeout should interrupt suspended coroutine scope").config(timeout = Duration.milliseconds(50)) {
         someCoroutine()
      }

      test("a testcase timeout should apply to the total time of all invocations").config(
         timeout = Duration.milliseconds(1000),
         invocations = 3
      ) {
         delay(500)
      }

      test("an invocation timeout should interrupt a test that otherwise would complete").config(
         invocationTimeout = Duration.milliseconds(100),
         timeout = Duration.milliseconds(5000),
         invocations = 3
      ) {
         delay(500)
      }

      test("a invocation timeout should apply even to a single invocation").config(
         invocationTimeout = Duration.milliseconds(100),
         timeout = Duration.milliseconds(5000),
         invocations = 1
      ) {
         delay(500)
      }

      test("a testcase timeout should apply if the cumulative sum of invocations is greater than the timeout value").config(
         invocationTimeout = Duration.milliseconds(250),
         timeout = Duration.milliseconds(2000),
         invocations = 100
      ) {
         // each of these delays is well within the 250 invocation timeout
         // but after 20 iterations we should pass the 2000 timeout value and die
         delay(50)
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
