package com.sksamuel.kotest.matchers

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.assertSoftly
import io.kotest.assertions.asClue
import io.kotest.assertions.withClue
import io.kotest.matchers.beInstanceOf
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.haveSameHashCodeAs
import io.kotest.matchers.string.haveLength
import io.kotest.matchers.string.shouldContain
import io.kotest.should
import io.kotest.shouldBe
import io.kotest.shouldNot
import io.kotest.shouldNotBe
import io.kotest.shouldThrow
import io.kotest.specs.FreeSpec
import java.util.ArrayList

class MatchersTest : FreeSpec({

  "withClue()" - {
    fun withClueEcho(other: String) = object : Matcher<String> {
      override fun test(value: String)= MatcherResult(false,
              "Should have the details of '$value' and $other",
              "Should have the details of '$value' and $other")
    }

    "should prepend clue to message with a newline" {
      val ex = shouldThrow<AssertionError> {
        withClue("a clue:") { "1" shouldBe withClueEcho("here are the details!") }
      }
      ex.message shouldBe "a clue:\nShould have the details of '1' and here are the details!"
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
        shouldContain("outer clue:\nShould have the details of '1' and the details!")
        shouldContain("inner clue:\nexpected: \"1\" but was: \"2\"")
      }
    }

    "should show all available nested clue contexts" {
      withClue("clue outer:") {
        shouldThrow<AssertionError> {"1" shouldBe "2" }.message shouldBe "clue outer:\nexpected: \"2\" but was: \"1\""
        withClue("clue inner:") {
          shouldThrow<AssertionError> {"3" shouldBe "4" }.message shouldBe "clue outer:\nclue inner:\nexpected: \"4\" but was: \"3\""
        }
        shouldThrow<AssertionError> {"5" shouldBe "6" }.message shouldBe "clue outer:\nexpected: \"6\" but was: \"5\""
      }
      //And resets completely when leaving final clue block
      shouldThrow<AssertionError> {"7" shouldBe "8" }.message shouldBe "expected: \"8\" but was: \"7\""
    }

  }
  "asClue()" - {
    "should prepend clue to message with a newline" {
      val ex = shouldThrow<AssertionError> {
        "a clue:".asClue { "1" shouldBe "2" }
      }
      ex.message shouldBe "a clue:\nexpected: \"2\" but was: \"1\""
    }

    "should add clues correctly with multiple/softAssert" {
      val ex = shouldThrow<AssertionError> {
        "outer clue:".asClue {
          assertSoftly {
            "1" shouldBe "the details"
            "inner clue:".asClue {"2" shouldBe "1"}
          }
        }
      }
      ex.message.apply {
        shouldContain("outer clue:\nexpected: \"the details\" but was: \"1\"")
        shouldContain("outer clue:\ninner clue:\nexpected: \"1\" but was: \"2\"")
      }
    }

    "should show all available nested clue contexts" {
      data class MyData(val a: Int, val b: String)
      MyData(10, "clue object").asClue {
        shouldThrow<AssertionError> {it.b shouldBe "2" }.message shouldBe "MyData(a=10, b=clue object)\nexpected: \"2\" but was: \"clue object\""
      }

      data class HttpResponse(val status: Int, val body: String)
      val response = HttpResponse(404, "not found")
      response.asClue {
        shouldThrow<AssertionError> {it.status shouldBe 200}.message shouldBe "HttpResponse(status=404, body=not found)\nexpected: 200 but was: 404"
        MyData(20, "nest it").asClue { inner ->
          shouldThrow<AssertionError> {it.status shouldBe 200}.message shouldBe "HttpResponse(status=404, body=not found)\nMyData(a=20, b=nest it)\nexpected: 200 but was: 404"
          shouldThrow<AssertionError> {inner.a shouldBe 10}.message shouldBe "HttpResponse(status=404, body=not found)\nMyData(a=20, b=nest it)\nexpected: 10 but was: 20"
        }
        //after nesting, everything looks as before
        shouldThrow<AssertionError> {it.status shouldBe 200}.message shouldBe "HttpResponse(status=404, body=not found)\nexpected: 200 but was: 404"
      }
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
        intArrayOf(1) shouldBe intArrayOf(2)
      }.message shouldBe "expected: [2] but was: [1]"
      shouldThrow<AssertionError> {
        charArrayOf('a') shouldBe charArrayOf('b')
      }.message shouldBe "expected: ['b'] but was: ['a']"
      shouldThrow<AssertionError> {
        byteArrayOf(1.toByte(), 35.toByte()) shouldBe byteArrayOf(12.toByte(), 13.toByte())
      }.message shouldBe "expected: [12, 13] but was: [1, 35]"
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

    "Should fail for equal primitive type" {
      shouldThrow<AssertionError> { byteArrayOf(1, 2, 3) shouldNotBe byteArrayOf(1, 2, 3) }
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
