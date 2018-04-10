package com.sksamuel.kotlintest.tests.assertions.arrow

import arrow.data.Invalid
import arrow.data.Valid
import io.kotlintest.assertions.arrow.validation.beInvalid
import io.kotlintest.assertions.arrow.validation.beValid
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
  }

  "Validated shouldBe Invalid" {

    shouldThrow<AssertionError> {
      Valid("foo") should beInvalid()
    }.message shouldBe "Valid(a=foo) should be Invalid"

    Invalid("error") should beInvalid()
  }
})