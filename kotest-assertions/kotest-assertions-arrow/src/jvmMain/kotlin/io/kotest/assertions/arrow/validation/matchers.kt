package io.kotest.assertions.arrow.validation

import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Validated<*, *>.shouldBeValid() {
   contract {
      returns() implies (this@shouldBeValid is Valid<*>)
   }
   this should beValid()
}

fun <T> Validated<*, T>.shouldNotBeValid() = this shouldNot beValid()

infix fun <T> Validated<*, T>.shouldBeValid(value: T) = this should beValid(value)
infix fun <T> Validated<*, T>.shouldNotBeValid(value: T) = this shouldNot beValid(value)

infix fun <T> Validated<*, T>.shouldBeValid(fn: (Valid<T>) -> Unit) {
   this.shouldBeValid()
   fn(this as Valid<T>)
}

fun <A> beValid() = object : Matcher<Validated<*, A>> {
   override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value is Valid, "$value should be Valid", "$value should not be Valid")
}

fun <A> beValid(a: A) = object : Matcher<Validated<*, A>> {
   override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value == Valid(a), "$value should be Valid($a)", "$value should not be Valid($a)")
}

@OptIn(ExperimentalContracts::class)
fun Validated<*, *>.shouldBeInvalid() {
   contract {
      returns() implies (this@shouldBeInvalid is Validated.Invalid<*>)
   }
   this should beInvalid()
}

infix fun <T> Validated<*, T>.shouldBeInvalid(value: T) = this should beInvalid(value)

infix fun <T> Validated<T, *>.shouldBeInvalid(fn: (Invalid<T>) -> Unit) {
   this.shouldBeInvalid()
   fn(this as Invalid<T>)
}

fun <A> beInvalid() = object : Matcher<Validated<*, A>> {
   override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value is Invalid, "$value should be Invalid", "$value should not be Invalid")
}

fun <A> beInvalid(a: A) = object : Matcher<Validated<*, A>> {
   override fun test(value: Validated<*, A>): MatcherResult =
      MatcherResult(value == Invalid(a), "$value should be Invalid($a)", "$value should not be Invalid($a)")
}
