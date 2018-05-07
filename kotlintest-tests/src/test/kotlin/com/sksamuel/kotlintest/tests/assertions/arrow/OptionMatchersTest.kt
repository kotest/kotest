package com.sksamuel.kotlintest.tests.assertions.arrow

import arrow.core.Option
import io.kotlintest.assertions.arrow.option.beNone
import io.kotlintest.assertions.arrow.option.beSome
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class OptionMatchersTest : WordSpec() {

  init {

    "Option shouldBe some(value)" should {
      "test that an option is a Some with the given value" {

        shouldThrow<AssertionError> {
          Option.empty<String>() shouldBe beSome("foo")
        }.message shouldBe "Option should be Some(foo) but was None"

        shouldThrow<AssertionError> {
          Option.just("boo") shouldBe beSome("foo")
        }.message shouldBe "Option should be Some(foo) but was Some(boo)"

        val option = Option.just("foo")
        option shouldBe beSome("foo")
      }
    }

    "Option shouldBe none()" should {
      "test that an option is a None" {

        shouldThrow<AssertionError> {
          Option.just("foo") shouldBe beNone()
        }.message shouldBe "Option should be None but was Some(foo)"

        Option.empty<String>() shouldBe beNone()
      }
    }
  }
}