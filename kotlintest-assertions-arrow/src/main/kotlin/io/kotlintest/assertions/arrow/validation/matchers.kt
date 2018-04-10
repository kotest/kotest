package io.kotlintest.assertions.arrow.validation

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import io.kotlintest.Matcher
import io.kotlintest.Result

fun <A> beValid() = object : Matcher<Validated<Any, A>> {
  override fun test(value: Validated<Any, A>): Result =
      Result(value is Valid, "$value should be Valid", "$value should not be Valid")
}

fun <A> beInvalid() = object : Matcher<Validated<Any, A>> {
  override fun test(value: Validated<Any, A>): Result =
      Result(value is Invalid, "$value should be Invalid", "$value should not be Invalid")
}