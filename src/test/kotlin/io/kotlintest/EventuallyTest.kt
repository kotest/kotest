package io.kotlintest

import io.kotlintest.specs.WordSpec
import io.kotlintest.Duration.Companion.days
import io.kotlintest.Duration.Companion.seconds

class EventuallyTest : WordSpec(), Eventually {

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
        shouldThrow<TestFailedException> {
          eventually(2.seconds) {
            throw RuntimeException("foo")
          }
        }
      }
    }
  }
}
