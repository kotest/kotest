package com.sksamuel.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.retry
import io.kotest.shouldBe
import java.time.Duration.ofMillis

class RetryTest : StringSpec() {
   init {
      "should call given assertion when until it pass in given number of times" {
         val testClass = TestClass(4)
         retry(5, ofMillis(500), ofMillis(100)) {
            testClass.isReady() shouldBe true
         }
      }

      "should not call given assertion beyond given number of times" {
         val testClass = TestClass(4)
         runSafely {
            retry(2, ofMillis(500), ofMillis(100), 1) {
               testClass.isReady() shouldBe true
            }
         }
         testClass.times shouldBe 2
      }

      "should not call given assertion beyond given max duration" {
         val testClass = TestClass(4)
         runSafely {
            retry(5, ofMillis(500), ofMillis(400), 1) {
               testClass.isReady() shouldBe true
            }
         }
         testClass.times shouldBe 2
      }

      "should call given assertion exponentially" {
         val testClass = TestClass(4)
         runSafely {
            retry(5, ofMillis(500), ofMillis(100), 2) {
               testClass.isReady() shouldBe true
            }
         }
         val calledAt = testClass.calledAtTimeInstance
         val timeHaltInFirstRetry = (calledAt[1] - calledAt[0])
         val timeHaltInSecondRetry = calledAt[2] - calledAt[1]
         timeHaltInFirstRetry shouldBeGreaterThanOrEqual 100
         timeHaltInSecondRetry shouldBeGreaterThanOrEqual 200
      }

      "should not retry in case of unexpected exception" {
         val testClass = TestClass(2)
         runSafely {
            retry(5, ofMillis(500), ofMillis(20), 1, IllegalArgumentException::class.java) {
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
}
