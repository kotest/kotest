package com.sksamuel.kotlintest.assertions.arrow

import arrow.data.Invalid
import arrow.data.Valid
import io.kotlintest.assertions.arrow.validation.beInvalid
import io.kotlintest.assertions.arrow.validation.beValid
import io.kotlintest.assertions.arrow.validation.shouldBeInvalid
import io.kotlintest.assertions.arrow.validation.shouldBeValid
import io.kotlintest.assertions.arrow.validation.shouldNotBeValid
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class ValidatedMatchersTest : StringSpec({

  "Validated shouldBe Valid" {

    shouldThrow<AssertionError> {
      Invalid("error") should beValid()
    }.message shouldBe "Invalid(e=error) should be Valid"

    Valid("ok") should beValid()
    Valid("ok").shouldBeValid()
    Valid("ok").shouldBeValid("ok")

    shouldThrow<AssertionError> {
      Valid("ok").shouldNotBeValid("ok")
    }.message shouldBe "Valid(a=ok) should not be Valid(a=ok)"

    shouldThrow<AssertionError> {
      Invalid("error") should beValid("error")
    }.message shouldBe "Invalid(e=error) should be Valid(a=error)"
  }

  "Validated shouldBe Invalid" {

    shouldThrow<AssertionError> {
      Valid("foo") should beInvalid()
    }.message shouldBe "Valid(a=foo) should be Invalid"

    Invalid("error") should beInvalid()
    Invalid("error").shouldBeInvalid()
  }
})