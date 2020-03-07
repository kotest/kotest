package com.sksamuel.kotest.assertions.timing

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.time.ExperimentalTime
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
        shouldThrow<KotlinNullPointerException> {
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
            if (count == 0)
              fail("first")
            else {
              count++
              fail("last")
            }
          }
        }.message?.endsWith("; underlying cause was expected: 2 but was: 1") shouldBe true
      }
      "allow suspendable functions" {
        eventually(2.seconds) {
          delay(1000)
          System.currentTimeMillis()
        }
      }
    }
  }
}
