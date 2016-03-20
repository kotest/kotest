package com.sksamuel.kotlintest

import com.sksamuel.kotlintest.Matchers.*
import java.util.*
import kotlin.collections.listOf

class MatchersTest : FreeSpec(), Matchers {
  init {
    "Matchers.shouldBe" - {
      "should compare equality" with {
        "a" shouldBe "a"
      }
    }
    "Matchers.shouldEqual" - {
      "should compare equality" with {
        "a" shouldEqual "a"
      }
    }
    "Matcher should have size x" - {
      "should compare sizes of iterables" with {
        listOf(1, 2, 3) should have size 3
      }
    }
    "Matchers should be an x" - {
      "should test that an instance is the required type" with {
        "bibble" should be a String::class
        ArrayList<String>() should be a List::class
      }
    }
  }
}