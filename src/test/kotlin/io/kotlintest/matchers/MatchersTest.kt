package io.kotlintest.matchers

import io.kotlintest.specs.FreeSpec
import java.util.*

class MatchersTest : FreeSpec() {

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
        shouldThrow<AssertionError> {
          val name: String? = "nullornot"
          name shouldBe null
        }
        shouldThrow<AssertionError> {
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
        listOf(1, 2, 3) should haveSize<Int>(3)
      }
    }

    "Matchers should be an x" - {
      "should test that an instance is the required type" {
        "bibble" should beInstanceOf(String::class)
        ArrayList<String>() should beInstanceOf(List::class)
      }
    }
  }
}