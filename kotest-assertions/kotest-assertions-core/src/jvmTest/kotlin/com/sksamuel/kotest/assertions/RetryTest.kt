package com.sksamuel.kotest.assertions

import io.kotest.assertions.retry
import io.kotest.assertions.retryConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class RetryTest : StringSpec() {
   init {

      "should allow execution of suspend functions" {
         retry(5, 500.milliseconds, 100.milliseconds) {
            dummySuspend()
         }

         retry(5, 500.milliseconds, 20.milliseconds, 1, IllegalArgumentException::class) {
            dummySuspend()
         }
      }

      "should allow config" {
         val config = retryConfig {
            maxRetry = 5
            timeout = 500.milliseconds
            delay = 100.milliseconds
         }
         retry(config) {
            delay(10)
         }
      }

      "should call given assertion when until it pass in given number of times" {
         val testClass = TestClass(4)
         retry(5, 500.milliseconds, 100.milliseconds) {
            testClass.isReady() shouldBe true
         }
      }

      "should not call given assertion beyond given number of times" {
         val testClass = TestClass(4)
         runSafely {
            retry(2, 500.milliseconds, 100.milliseconds, 1) {
               testClass.isReady() shouldBe true
            }
         }
         testClass.times shouldBe 2
      }

      "should not call given assertion beyond given max duration" {
         val testClass = TestClass(4)
         runSafely {
            retry(5, 500.milliseconds, 400.milliseconds, 1) {
               testClass.isReady() shouldBe true
            }
         }
         testClass.times shouldBe 2
      }

      "should call given assertion exponentially" {
         val testClass = TestClass(4)
         runSafely {
            retry(5, 500.milliseconds, 100.milliseconds, 2) {
               testClass.isReady() shouldBe true
            }
         }
         val calledAt = testClass.calledAtTimeInstance
         val delayInFirstRetry = (calledAt[1] - calledAt[0])
         val delayInSecondRetry = calledAt[2] - calledAt[1]
         delayInFirstRetry shouldBeGreaterThanOrEqual 100
         delayInSecondRetry shouldBeGreaterThanOrEqual 200
      }

      "should not retry in case of unexpected exception" {
         val testClass = TestClass(2)
         runSafely {
            retry(5, 500.milliseconds, 20.milliseconds, 1, IllegalArgumentException::class) {
               testClass.throwUnexpectedException()
            }
         }

         testClass.times shouldBe 1
      }
   }

   private class TestClass(private val readyAfter: Int) {
      var calledAtTimeInstance = listOf<Long>()
      var times = 0
      fun isReady(): Boolean {
         calledAtTimeInstance = calledAtTimeInstance.plus(System.currentTimeMillis())
         times += 1
         return readyAfter == times
      }

      fun throwUnexpectedException() {
         times += 1
         throw NullPointerException("")
      }
   }

   private suspend fun runSafely(block: suspend () -> Unit) {
      try {
         block()
      } catch (assertionError: AssertionError) {
         // Eating assertion error
      } catch (exception: Exception) {
         // Eating other exception
      }
   }

   private suspend fun dummySuspend() {
      delay(0)
   }
}
