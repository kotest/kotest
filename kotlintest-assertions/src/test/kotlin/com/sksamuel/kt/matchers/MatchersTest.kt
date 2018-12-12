package com.sksamuel.kt.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.collections.haveSize
import io.kotlintest.matchers.haveLength
import io.kotlintest.matchers.haveSameHashCodeAs
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.withClue
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import java.util.ArrayList

class MatchersTest : FreeSpec({

  "withClue()" - {
    fun withClueEcho(other: String) = object : Matcher<String> {
      override fun test(value: String)= Result(false,
              "Should have the details of '$value' and $other",
              "Should have the details of '$value' and $other")
    }

    "should prepend clue to message" {
      val ex = shouldThrow<AssertionError> {
        withClue("a clue:") { "1" shouldBe withClueEcho("here are the details!") }
      }
      ex.message shouldBe "a clue: Should have the details of '1' and here are the details!"
    }

    "should add clues correctly with multiple/softAssert" {
      val ex = shouldThrow<AssertionError> {
        withClue("outer clue:") {
          assertSoftly {
            "1" shouldBe withClueEcho("the details!")
            withClue("inner clue:") {"2" shouldBe "1"}
          }
        }
      }
      ex.message.apply {
        shouldContain("outer clue: Should have the details of '1' and the details!")
        shouldContain("inner clue: expected: \"1\" but was: \"2\"")
      }
    }

    "should remember previous clue contexts" {
      withClue("clue outer:") {
        shouldThrow<AssertionError> {"1" shouldBe "2" }.message shouldBe "clue outer: expected: \"2\" but was: \"1\""
        withClue("clue inner:") {
          shouldThrow<AssertionError> {"3" shouldBe "4" }.message shouldBe "clue inner: expected: \"4\" but was: \"3\""
        }
        shouldThrow<AssertionError> {"5" shouldBe "6" }.message shouldBe "clue outer: expected: \"6\" but was: \"5\""
      }
      //And resets completely when leaving final clue block
      shouldThrow<AssertionError> {"7" shouldBe "8" }.message shouldBe "expected: \"8\" but was: \"7\""
    }

  }

  "haveSameHashCode()" {
    1 should haveSameHashCodeAs(1)
    2 shouldNot haveSameHashCodeAs(1)
  }

  "support 'or' function on matcher" {
    "hello" should haveLength(5).or(haveLength(6))
  }

  "Matchers.shouldBe" - {

    "should compare equality" {
      "a" shouldBe "a"

      shouldThrow<AssertionError> {
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
        val name = "notnull"
        name shouldBe null
      }
    }

    "formats value representations" {
      shouldThrow<AssertionError> {
        1f shouldBe 2f
      }.message shouldBe "expected: 2.0f but was: 1.0f"
      shouldThrow<AssertionError> {
        1L shouldBe 2L
      }.message shouldBe "expected: 2L but was: 1L"
      shouldThrow<AssertionError> {
        'a' shouldBe 'b'
      }.message shouldBe "expected: 'b' but was: 'a'"
      shouldThrow<AssertionError> {
        "a" shouldBe "b"
      }.message shouldBe "expected: \"b\" but was: \"a\""
      shouldThrow<AssertionError> {
        arrayOf("a") shouldBe arrayOf("b")
      }.message shouldBe "expected: [\"b\"] but was: [\"a\"]"
      shouldThrow<AssertionError> {
        floatArrayOf(1f) shouldBe floatArrayOf(2f)
      }.message shouldBe "expected: [2.0f] but was: [1.0f]"
      shouldThrow<AssertionError> {
        longArrayOf(1L) shouldBe longArrayOf(2L)
      }.message shouldBe "expected: [2L] but was: [1L]"
      shouldThrow<AssertionError> {
        charArrayOf('a') shouldBe charArrayOf('b')
      }.message shouldBe "expected: ['b'] but was: ['a']"
      shouldThrow<AssertionError> {
        listOf('a') shouldBe listOf('b')
      }.message shouldBe "expected: ['b'] but was: ['a']"
      shouldThrow<AssertionError> {
        mapOf('a' to 1L) shouldBe mapOf('b' to 2L)
      }.message shouldBe "expected: {'b'=2L} but was: {'a'=1L}"
      shouldThrow<AssertionError> {
        val l = ArrayList<Any>()
        l.add(l)
        l shouldBe emptyList<Any>()
      }.message shouldBe "expected: [] but was: [(this ArrayList)]"
      shouldThrow<AssertionError> {
        val l = HashMap<Any, Any>()
        l[1L] = l
        l shouldBe emptyMap<Any, Any>()
      }.message shouldBe "expected: {} but was: {1L=(this HashMap)}"
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

})
