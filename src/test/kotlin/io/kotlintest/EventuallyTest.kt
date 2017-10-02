package io.kotlintest

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.matchers.shouldThrow
import java.io.FileNotFoundException
import java.io.IOException

class EventuallyTest : WordSpec() {

  init {
    "eventually" should {
      "pass working tests"  {
        eventually(5.days) {
          System.currentTimeMillis()
        }
      }
      "pass tests that completed within the time allowed"  {
        val end = System.currentTimeMillis() + 2000
        eventually(5.days) {
          if (System.currentTimeMillis() < end)
            throw RuntimeException("foo")
        }
      }
      "fail tests that do not complete within the time allowed"  {
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
      "pass tests that completed within the time allowed, custom exception"  {
        val end = System.currentTimeMillis() + 2000
        eventually(5.days, AssertionError::class.java) {
          if (System.currentTimeMillis() < end)
            assert(false)
        }
      }
      "fail tests throw unexpected exception type"  {
        shouldThrow<KotlinNullPointerException> {
          eventually(2.seconds, IOException::class.java) {
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
    }
  }
}
