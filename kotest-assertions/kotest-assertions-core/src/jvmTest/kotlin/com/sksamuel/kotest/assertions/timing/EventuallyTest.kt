package com.sksamuel.kotest.assertions.timing

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.days
import kotlin.time.milliseconds
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class EventuallyTest : WordSpec() {

   init {
      "eventually" should {
         "pass working tests" {
            eventually(5.days) {
               System.currentTimeMillis()
            }
         }
         "pass tests that completed within the time allowed"  {
            val end = System.currentTimeMillis() + 2000
            eventually(3.seconds) {
               if (System.currentTimeMillis() < end)
                  throw RuntimeException("foo")
            }
         }
         "fail tests that do not complete within the time allowed" {
            shouldThrow<AssertionError> {
               eventually(2.seconds) {
                  throw RuntimeException("foo")
               }
            }
         }
         "return the result computed inside" {
            val result = eventually(2.seconds) {
               1
            }
            result shouldBe 1
         }
         "pass tests that completed within the time allowed, AssertionError"  {
            val end = System.currentTimeMillis() + 2000
            eventually(5.days) {
               if (System.currentTimeMillis() < end)
                  assert(false)
            }
         }
         "pass tests that completed within the time allowed, custom exception"  {
            val end = System.currentTimeMillis() + 2000
            eventually(5.seconds, FileNotFoundException::class) {
               if (System.currentTimeMillis() < end)
                  throw FileNotFoundException()
            }
         }
         "fail tests throw unexpected exception type"  {
            shouldThrow<NullPointerException> {
               eventually(2.seconds, IOException::class) {
                  (null as String?)!!.length
               }
            }
         }
         "pass tests that throws FileNotFoundException for some time"  {
            val end = System.currentTimeMillis() + 2000
            eventually(5.days) {
               if (System.currentTimeMillis() < end)
                  throw FileNotFoundException("foo")
            }
         }
         "handle kotlin assertion errors" {
            var thrown = false
            eventually(100.milliseconds) {
               if (!thrown) {
                  thrown = true
                  throw AssertionError("boom")
               }
            }
         }
         "handle java assertion errors" {
            var thrown = false
            eventually(100.milliseconds) {
               if (!thrown) {
                  thrown = true
                  throw java.lang.AssertionError("boom")
               }
            }
         }
         "display the first and last underlying failures" {
            var count = 0
            shouldThrow<AssertionError> {
               eventually(100.milliseconds) {
                  if (count == 0) {
                     count = 1
                     fail("first")
                  } else {
                     fail("last")
                  }
               }
            }.message.shouldMatch("Test failed after 100ms; attempted \\d+ times; first cause was first; last cause was last".toRegex())
         }
         "allow suspendable functions" {
            eventually(2.seconds) {
               delay(1000)
               System.currentTimeMillis()
            }
         }
         "allow configuring poll delay" {
            var count = 0
            eventually(2.seconds, 400.milliseconds) {
               count += 1
            }
            count.shouldBeLessThan(6)
         }
         "handle shouldNotBeNull" {
            val mark = TimeSource.Monotonic.markNow()
            shouldThrow<java.lang.AssertionError> {
               eventually(2.seconds) {
                  val str: String? = null
                  str.shouldNotBeNull()
               }
            }
            mark.elapsedNow().toLongMilliseconds().shouldBeGreaterThanOrEqual(2000)
         }
      }
   }
}
