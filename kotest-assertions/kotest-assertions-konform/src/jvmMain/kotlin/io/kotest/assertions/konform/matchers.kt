package io.kotest.assertions.konform

import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should

infix fun <T> Validation<T>.shouldBeValid(value: T) = this should beValid(value)

fun <A> beValid(a: A) = object : Matcher<Validation<A>> {
   override fun test(value: Validation<A>): MatcherResult =
      MatcherResult(value(a) is Valid, "$a should be valid", "$a should not be valid")
}

infix fun <T> Validation<T>.shouldBeInvalid(value: T) = this should beInvalid(value)

fun <A> beInvalid(a: A) = object : Matcher<Validation<A>> {
   override fun test(value: Validation<A>): MatcherResult =
      MatcherResult(value(a) is Invalid, "$a should be invalid", "$a should not be invalid")
}

inline fun <T> Validation<T>.shouldBeInvalid(value: T, fn: (Invalid<T>) -> Unit) {
   this.shouldBeInvalid(value)
   fn(this(value) as Invalid<T>)
}
