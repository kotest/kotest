package com.sksamuel.kotlintest.assertions.arrow

import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.invalid
import arrow.core.valid
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
    Valid("ok") shouldBeValid "ok"
    Valid("ok") shouldBeValid { it.a == "ok" }

    shouldThrow<AssertionError> {
      Valid("ok") shouldNotBeValid "ok"
    }.message shouldBe "Valid(a=ok) should not be Valid(a=ok)"

    shouldThrow<AssertionError> {
      Invalid("error") should beValid("error")
    }.message shouldBe "Invalid(e=error) should be Valid(a=error)"
  }

  "Validated should use contracts to smart cast Valids" {
    val e = "boo".valid()
    e.shouldBeValid()
    e.a shouldBe "boo"
  }

  "Validated shouldBe Invalid" {

    shouldThrow<AssertionError> {
      Valid("foo") should beInvalid()
    }.message shouldBe "Valid(a=foo) should be Invalid"

    Invalid("error") should beInvalid()
    Invalid("error").shouldBeInvalid()
    Invalid("error") shouldBeInvalid "error"
    Invalid("error") shouldBeInvalid { it.e == "error" }
    Invalid("error").shouldNotBeValid()
  }

  "use contracts to smart cast Invalids" {
    val e = "boo".invalid()
    e.shouldBeInvalid()
    e.e shouldBe "boo"
  }
})
