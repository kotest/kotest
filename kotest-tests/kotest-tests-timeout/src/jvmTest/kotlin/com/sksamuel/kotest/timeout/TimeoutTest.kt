package com.sksamuel.kotest.timeout

import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class TimeoutTest : StringSpec() {

   init {

      extension(expectFailureExtension)

      "a testcase timeout should interrupt a blocked thread".config(timeout = 250.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         Thread.sleep(1000000)
      }

      "a testcase timeout should interrupt a suspended coroutine".config(timeout = 250.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         delay(1000000)
      }

      "a testcase timeout should interrupt suspended nested coroutines".config(timeout = 250.milliseconds) {
         // high value to ensure its interrupted, we'd notice a test that runs for 10 weeks
         someCoroutine()
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
