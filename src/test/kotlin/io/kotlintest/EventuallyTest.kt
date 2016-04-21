package io.kotlintest

import io.kotlintest.specs.WordSpec
import java.util.concurrent.TimeUnit

class EventuallyTest : WordSpec(), Eventually {

  init {
    "eventually" should {
      "pass working tests"  {
        eventually(5, TimeUnit.DAYS) {
          System.currentTimeMillis()
        }
      }
      "pass tests that completed within the time allowed"  {
        val end = System.currentTimeMillis() + 2000
        eventually(5, TimeUnit.DAYS) {
          if (System.currentTimeMillis() < end)
            throw RuntimeException("foo")
        }
      }
      "fail tests that do not complete within the time allowed"  {
        expecting(TestFailedException::class) {
          eventually(2, TimeUnit.SECONDS) {
            throw RuntimeException("foo")
          }
        }
      }
    }
  }
}
