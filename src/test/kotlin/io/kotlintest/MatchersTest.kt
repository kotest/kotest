package io.kotlintest

import io.kotlintest.matchers.Matchers
import io.kotlintest.matchers.be
import io.kotlintest.matchers.have
import io.kotlintest.specs.FreeSpec
import java.util.*
import kotlin.collections.listOf

class MatchersTest : FreeSpec(), Matchers {
  init {
    "Matchers.shouldBe" - {
      "should compare equality" {
        "a" shouldBe "a"
      }
      "should support matching null with null" {
        val name: String? = null
        name shouldBe null
      }
      "should support matching non null with null" {
        expecting(TestFailedException::class) {
          val name: String? = "nullornot"
          name shouldBe null
        }
        expecting(TestFailedException::class) {
          val name: String = "notnull"
          name shouldBe null
        }
      }
    }
    "Matchers.shouldEqual" - {
      "should compare equality" {
        "a" shouldEqual "a"
      }
    }
    "Matcher should have size x" - {
      "should compare sizes of iterables" {
        listOf(1, 2, 3) should have size 3
      }
    }
    "Matchers should be an x" - {
      "should test that an instance is the required type" {
        "bibble" should be a String::class
        ArrayList<String>() should be a List::class
      }
    }
  }
}