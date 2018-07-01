package com.sksamuel.kotlintest.matchers

import io.kotlintest.matchers.beLessThan
import io.kotlintest.matchers.collections.containExactly
import io.kotlintest.matchers.collections.shouldNotContainExactly
import io.kotlintest.matchers.doubles.negative
import io.kotlintest.matchers.doubles.positive
import io.kotlintest.matchers.doubles.shouldBeNegative
import io.kotlintest.matchers.endWith
import io.kotlintest.matchers.haveKey
import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.matchers.numerics.shouldBePositive
import io.kotlintest.matchers.string.contain
import io.kotlintest.matchers.string.shouldNotEndWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FreeSpec
import io.kotlintest.verifyAll

class SoftAssertionsTest : FreeSpec({

  "verifyAll" - {

    "passes when all assertions pass" {
      verifyAll {
        1 shouldBe 1
        "foo" shouldBe "foo"
      }
    }

    "rethrows single failures" {
      shouldThrow<AssertionError> {
        verifyAll {
          1 shouldBe 2
        }
      }.message shouldBe "expected: 2 but was: 1"
    }

    "groups multiple failures" {
      shouldThrow<AssertionError> {
        verifyAll {
          1 shouldBe 2
          1 shouldBe 1 // should pass
          "foo" shouldNotBe "foo"
        }
      }.let {
        it.message should contain("1) expected: 2 but was: 1")
        it.message should contain("2) \"foo\" should not equal \"foo\"")
      }
    }

    "works with all array types" {
      shouldThrow<AssertionError> {
        verifyAll {
          booleanArrayOf(true) shouldBe booleanArrayOf(false)
          intArrayOf(1) shouldBe intArrayOf(2)
          shortArrayOf(1) shouldBe shortArrayOf(2)
          floatArrayOf(1f) shouldBe floatArrayOf(2f)
          doubleArrayOf(1.0) shouldBe doubleArrayOf(2.0)
          longArrayOf(1) shouldBe longArrayOf(2)
          byteArrayOf(1) shouldBe byteArrayOf(2)
          charArrayOf('a') shouldBe charArrayOf('b')
          arrayOf("foo") shouldBe arrayOf("bar")
        }
      }.let {
        it.message should contain("9) expected: [\"bar\"] but was: [\"foo\"]")
        it.message shouldNot contain("10) ")
      }
    }

    "works with any matcher" {
      shouldThrow<AssertionError> {
        verifyAll {
          1 should beLessThan(0)
          "foobar" shouldNot endWith("bar")
          1 shouldBe positive() // should pass
          1.0 shouldBe negative()
          listOf(1) shouldNot containExactly(1)
          mapOf(1 to 2) should haveKey(3)
        }
      }.let {
        it.message should contain("5) Map should contain key 3")
        it.message shouldNot contain("6) ")
      }
    }

    "works with extension functions" {
      shouldThrow<AssertionError> {
        verifyAll {
          1.shouldBeLessThan(0)
          "foobar".shouldNotEndWith("bar")
          1.shouldBePositive() // should pass
          1.0.shouldBeNegative()
          listOf(1).shouldNotContainExactly(1)
        }
      }.let {
        it.message should contain("4) Collection should not be exactly [1]")
        it.message shouldNot contain("5) ")
      }
    }

    "can be nested" {
      shouldThrow<AssertionError> {
        verifyAll {
          1 shouldBe 2
          verifyAll {
            2 shouldBe 3
          }
        }
      }.let {
        it.message should contain("1) expected: 2 but was: 1")
        it.message should contain("2) expected: 3 but was: 2")
      }
    }
  }
})
