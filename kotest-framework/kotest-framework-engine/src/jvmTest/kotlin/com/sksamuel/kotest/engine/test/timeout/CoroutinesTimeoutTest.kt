package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class CoroutinesTimeoutTest : FunSpec() {

   init {

      extension(expectFailureExtension)

      test("a testcase timeout should interrupt a blocked thread").config(
         timeout = 10.milliseconds,
         blockingTest = true
      ) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      test("a testcase timeout should interrupt a suspend function").config(timeout = 10.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      test("a testcase timeout should interupt a nested coroutine").config(timeout = 10.milliseconds) {
         launch {
            // a high value to ensure its interrupted, we'd notice a test that runs forever
            delay(10.hours)
         }
      }

      test("a testcase timeout should interupt a deeply nested coroutine").config(timeout = 10.milliseconds) {
         launch {
            launch {
               // a high value to ensure its interrupted, we'd notice a test that runs forever
               delay(10.hours)
            }
         }
      }

      test("a testcase timeout should interrupt suspended coroutine scope").config(timeout = 10.milliseconds) {
         someCoroutine()
      }

      test("a testcase timeout should apply to the total time of all invocations").config(
         timeout = 10.milliseconds,
         invocations = 3
      ) {
         delay(9)
      }

      test("an invocation timeout should interrupt a test that otherwise would complete").config(
         invocationTimeout = 1.milliseconds,
         timeout = 1.hours,
         invocations = 3
      ) {
         delay(1.hours)
      }

      test("a invocation timeout should apply even to a single invocation").config(
         invocationTimeout = 1.milliseconds,
         timeout = 10000.milliseconds,
         invocations = 1
      ) {
         delay(1.hours)
      }

      test("a testcase timeout should apply if the cumulative sum of invocations is greater than the timeout value").config(
         invocationTimeout = 10.milliseconds,
         timeout = 20.milliseconds,
         invocations = 100
      ) {
         // each of these delays is well within the 10ms invocation timeout
         // but after some iterations we should pass the 20ms timeout for all invocations value and die
         delay(1)
      }
   }
}

private suspend fun someCoroutine() {
   coroutineScope {
      launch {
         delay(10000000)
      }
   }
}
