package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.hours
import kotlin.time.milliseconds

class TimeoutTest : FunSpec() {

   init {

      extension(expectFailureExtension)

      test("a testcase timeout should interrupt a blocked thread").config(timeout = 50.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      test("a testcase timeout should interrupt a suspend function").config(timeout = 50.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      test("a testcase timeout should interupt a nested coroutine").config(timeout = 50.milliseconds) {
         launch {
            // a  high value to ensure its interrupted, we'd notice a test that runs for ever
            delay(10.hours)
         }
      }

      test("a testcase timeout should interrupt suspended coroutine scope").config(timeout = 50.milliseconds) {
         someCoroutine()
      }

      test("a testcase timeout should apply to the total time of all invocations").config(
         timeout = 1000.milliseconds,
         invocations = 3
      ) {
         delay(500)
      }

      test("an invocation timeout should interrupt a test that otherwise would complete").config(
         invocationTimeout = 100.milliseconds,
         timeout = 5000.milliseconds,
         invocations = 3
      ) {
         delay(500)
      }

      test("a invocation timeout should apply even to a single invocation").config(
         invocationTimeout = 100.milliseconds,
         timeout = 5000.milliseconds,
         invocations = 1
      ) {
         delay(500)
      }

      test("a testcase timeout should apply if the cumulative sum of invocations is greater than the timeout value").config(
         invocationTimeout = 250.milliseconds,
         timeout = 2000.milliseconds,
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
