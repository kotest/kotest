package com.sksamuel.kotlintest.tests.assertions.arrow

import arrow.core.Option
import io.kotlintest.assertions.arrow.option.none
import io.kotlintest.assertions.arrow.option.some
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec

class OptionMatchersTest : WordSpec() {

  init {

    "Option shouldBe some(value)" should {
      "test that an option is a Some with the given value" {

        shouldThrow<AssertionError> {
          Option.empty<String>() shouldBe some("foo")
        }.message shouldBe "Option should be Some(foo) but was None"

        shouldThrow<AssertionError> {
          Option.pure("boo") shouldBe some("foo")
        }.message shouldBe "Option should be Some(foo) but was Some(boo)"

        Option.pure("foo") shouldBe some("foo")
      }
    }

    "Option shouldBe none()" should {
      "test that an option is a None" {

        shouldThrow<AssertionError> {
          Option.pure("foo") shouldBe none()
        }.message shouldBe "Option should be None but was Some(foo)"

        Option.empty<String>() shouldBe none()
      }
    }
  }
}