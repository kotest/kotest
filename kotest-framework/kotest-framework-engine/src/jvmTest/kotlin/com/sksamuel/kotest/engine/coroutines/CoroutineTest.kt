package com.sksamuel.kotest.engine.coroutines

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.time.Duration.Companion.milliseconds

// tests kotest's interaction with coroutines
@OptIn(DelicateCoroutinesApi::class)
@EnabledIf(LinuxCondition::class)
class CoroutineTest : FunSpec() {
   init {

      aroundTest { (testCase, execute) ->
         val result = execute(testCase)
         when {
            testCase.name.name == "exceptions inside launched coroutine should be propagated" &&
               result.isError -> TestResult.Success(0.milliseconds)
            testCase.name.name == "exception in launched coroutine should cancel siblings" &&
               result.isError -> TestResult.Success(0.milliseconds)
            testCase.name.name == "exception in test coroutine should cancel launched coroutines" &&
               result.isError -> TestResult.Success(0.milliseconds)
            else -> result
         }
      }

      test("multiple coroutines should be launchable from a test") {
         launch {
            delay(10)
         }
         launch {
            delay(10)
         }
      }

      test("multiple coroutines should be able to use semaphores") {
         val sem = Semaphore(1)
         launch {
            sem.withPermit {
               delay(10)
            }
         }
         launch {
            sem.withPermit {
               delay(10)
            }
         }
      }

      test("exceptions inside launched coroutine should be propagated") {
         launch {
            delay(10)
            error("boom")
         }
      }

      test("exception in launched coroutine should cancel siblings") {
         launch {
            delay(1000000000)
         }
         launch {
            delay(10)
            error("boom") // this should cause the cancellation of its sibling above
         }
      }

      test("exception in test coroutine should cancel launched coroutines") {
         launch {
            delay(1000000000)
         }
         delay(10)
         error("boom") // this should cause the cancellation of its sibling above
      }

      test("channels should be usable in coroutines") {
         val channel: Channel<Int> = Channel()
         coroutineScope {
            launch {
               channel.send(1)
               channel.close()
            }
            launch {
               channel.receive()
            }
         }
         channel.isClosedForSend shouldBe true
      }
   }
}
