package com.sksamuel.kotest.assertions.arrow

import arrow.core.Option
import io.kotest.assertions.arrow.option.beNone
import io.kotest.assertions.arrow.option.beSome
import io.kotest.assertions.arrow.option.shouldBeNone
import io.kotest.assertions.arrow.option.shouldBeSome
import io.kotest.assertions.arrow.option.shouldNotBeNone
import io.kotest.assertions.arrow.option.shouldNotBeSome
import io.kotest.shouldBe
import io.kotest.shouldNotBe
import io.kotest.shouldThrow
import io.kotest.specs.WordSpec

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
