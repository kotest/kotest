package io.kotlintest.assertions.arrow.validation

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.should
import io.kotlintest.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@UseExperimental(ExperimentalContracts::class)
fun Validated<*, *>.shouldBeValid() {
  contract {
    returns() implies (this@shouldBeValid is Valid<*>)
  }
  this should beValid()
}

fun <T> Validated<*, T>.shouldNotBeValid() = this shouldNot beValid()

fun <T> Validated<*, T>.shouldBeValid(value: T) = this should beValid(value)
fun <T> Validated<*, T>.shouldNotBeValid(value: T) = this shouldNot beValid(value)

fun <T> Validated<*, T>.shouldBeValid(fn: (Valid<T>) -> Unit) {
  this.shouldBeValid()
  fn(this as Valid<T>)
}

fun <A> beValid() = object : Matcher<Validated<*, A>> {
  override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value is Valid, "$value should be Valid", "$value should not be Valid")
}

fun <A> beValid(a: A) = object : Matcher<Validated<*, A>> {
  override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value == Valid(a), "$value should be Valid(a=$a)", "$value should not be Valid(a=$a)")
}

@UseExperimental(ExperimentalContracts::class)
fun Validated<*, *>.shouldBeInvalid() {
  contract {
    returns() implies (this@shouldBeInvalid is Invalid<*>)
  }
  this should beInvalid()
}
fun Validated<*, *>.shouldNotBeInvalid() = this shouldNot beInvalid()

fun <T> Validated<*, T>.shouldBeInvalid(value: T) = this should beInvalid(value)
fun <T> Validated<*, T>.shouldNotBeInvalid(value: T) = this shouldNot beInvalid(value)

fun <T> Validated<T, *>.shouldBeInvalid(fn: (Invalid<T>) -> Unit) {
  this.shouldBeInvalid()
  fn(this as Invalid<T>)
}

fun <A> beInvalid() = object : Matcher<Validated<*, A>> {
  override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value is Invalid, "$value should be Invalid", "$value should not be Invalid")
}

fun <A> beInvalid(a: A) = object : Matcher<Validated<*, A>> {
  override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value == Invalid(a), "$value should be Invalid(a=$a)", "$value should not be Invalid(a=$a)")
}
