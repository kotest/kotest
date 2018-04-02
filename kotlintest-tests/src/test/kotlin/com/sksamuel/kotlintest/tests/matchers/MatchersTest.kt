package com.sksamuel.kotlintest.tests.matchers

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.haveSameHashCode
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.should
import io.kotlintest.specs.FreeSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import org.junit.ComparisonFailure
import java.util.*

class MatchersTest : FreeSpec() {

  init {

    "haveSameHashCode()" {
      1 should haveSameHashCode(1)
      2 shouldNot haveSameHashCode(1)
    }

    "Matchers.shouldBe" - {

      "should compare equality" {
        "a" shouldBe "a"

        shouldThrow<ComparisonFailure> {
          "a" shouldBe "b"
        }

        123 shouldBe 123

        shouldThrow<AssertionError> {
          123 shouldBe 456
        }
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

    "Matchers.shouldNotBe" - {
      "should compare equality" {
        "a" shouldNotBe "b"
        123 shouldNotBe 456

        shouldThrow<AssertionError> {
          "a" shouldNotBe "a"
        }

        shouldThrow<AssertionError> {
          123 shouldNotBe 123
        }
      }

      "should support (not) matching null with non-null" {
        "a" shouldNotBe null
      }

      "should support (not) matching non-null with null" {
        null shouldNotBe "a"
      }

      "should support (not) matching null with null" {
        shouldThrow<AssertionError> {
          null shouldNotBe null
        }
      }
    }

    "Matchers.shouldEqual" - {
      "should compare equality" {
        "a" shouldBe "a"
      }
    }

    "Matcher should have size x" - {
      "should compare sizes of iterables" {
        listOf(1, 2, 3) should haveSize(3)
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
