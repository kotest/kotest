package com.sksamuel.kotlintest.assertions.arrow

import arrow.core.Option
import io.kotlintest.assertions.arrow.option.beNone
import io.kotlintest.assertions.arrow.option.beSome
import io.kotlintest.assertions.arrow.option.shouldBeNone
import io.kotlintest.assertions.arrow.option.shouldBeSome
import io.kotlintest.assertions.arrow.option.shouldNotBeNone
import io.kotlintest.assertions.arrow.option.shouldNotBeSome
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class OptionMatchersTest : WordSpec() {

  init {

    "Option.shouldBeSome()" should {
      "use contracts" {
        val o = Option("foo")
        o.shouldBeSome()
        o.t shouldBe "foo"
      }
    }

    "Option shouldBe some(value)" should {
      "test that an option is a Some with the given value" {

        shouldThrow<AssertionError> {
          Option.empty<String>() shouldBe beSome("foo")
        }.message shouldBe "Option should be Some(foo) but was None"

        shouldThrow<AssertionError> {
          Option.empty<String>() shouldBeSome "foo"
        }.message shouldBe "Option should be Some(foo) but was None"

        shouldThrow<AssertionError> {
          Option.just("boo") shouldBe beSome("foo")
        }.message shouldBe "Option should be Some(foo) but was Some(boo)"

        val option = Option.just("foo")
        option shouldBe beSome("foo")
        option shouldBeSome "foo"

        option shouldBeSome { it == "foo" }
      }
    }

    "Option shouldNotBe some(value)" should {
      "test that an option is not a Some with the given value" {

        val option = Option.just("foo")
        option shouldNotBe beSome("bar")
        option shouldNotBeSome "bar"
      }
    }

    "Option shouldBe none()" should {
      "test that an option is a None" {

        shouldThrow<AssertionError> {
          Option.just("foo") shouldBe beNone()
        }.message shouldBe "Option should be None but was Some(foo)"

        Option.empty<String>() shouldBe beNone()
        Option.empty<String>().shouldBeNone()
      }
    }

    "Option shouldNotBe none()" should {
      "test that an option is not a None" {
        val option = Option.just("foo")

        option shouldNotBe beNone()
        option.shouldNotBeNone()
      }
    }
  }
}
