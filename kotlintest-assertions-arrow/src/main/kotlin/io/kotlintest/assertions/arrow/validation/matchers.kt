package io.kotlintest.assertions.arrow.validation

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

fun Validated<Any, Any>.shouldBeValid() = this should beValid()
fun Validated<Any, Any>.shouldNotBeValid() = this shouldNot beValid()

fun <T> Validated<Any, T>.shouldBeValid(value: T) = this should beValid(value)
fun <T> Validated<Any, T>.shouldNotBeValid(value: T) = this shouldNot beValid(value)

fun <A> beValid() = object : Matcher<Validated<Any, A>> {
  override fun test(value: Validated<Any, A>): Result =
      Result(value is Valid, "$value should be Valid", "$value should not be Valid")
}

fun <A> beValid(a: A) = object : Matcher<Validated<Any, A>> {
  override fun test(value: Validated<Any, A>): Result =
      Result(value == Valid(a), "$value should be Valid(a=$a)", "$value should not be Valid(a=$a)")
}

fun Validated<Any, Any>.shouldBeInvalid() = this should beInvalid()
fun Validated<Any, Any>.shouldNotBeInvalid() = this shouldNot beInvalid()
fun <A> beInvalid() = object : Matcher<Validated<Any, A>> {
  override fun test(value: Validated<Any, A>): Result =
      Result(value is Invalid, "$value should be Invalid", "$value should not be Invalid")
}