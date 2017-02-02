package io.kotlintest

import io.kotlintest.specs.WordSpec
import io.kotlintest.matchers.shouldThrow

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
    }
  }
}
