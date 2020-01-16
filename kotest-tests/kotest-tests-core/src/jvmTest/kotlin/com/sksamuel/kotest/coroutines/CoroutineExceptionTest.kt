package com.sksamuel.kotest.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
class CoroutineExceptionTest : FunSpec({

   extension { testCase, execute, complete ->
      execute(testCase) { result ->
         when (result.status) {
            TestStatus.Failure, TestStatus.Error -> complete(TestResult.success(Duration.ZERO))
            else -> complete(TestResult.failure(AssertionError("Should not happen"), Duration.ZERO))
         }
      }
   }

   test("exception in coroutine") {
      launch {
         println("A")
         delay(10000)
         println("B")
         error("boom")
      }
   }
})

fun main() {
   try {
      runBlocking {
         coroutineScope {
            println("launching a")
            launch {
               println("A")
               delay(2500)
            }
            println("launching b")
            launch {
               println("B")
               delay(100)
               error("boom")
            }
         }
         println("scope complete")
      }
   } catch (e: Throwable) {
      println(e)
   }
}
