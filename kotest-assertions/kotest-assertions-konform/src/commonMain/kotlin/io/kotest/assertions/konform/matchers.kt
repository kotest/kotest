package io.kotest.assertions.konform

import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should

infix fun <T> Validation<T>.shouldBeValid(value: T) = this should beValid(value)

fun <A> beValid(a: A) = object : Matcher<Validation<A>> {
   override fun test(value: Validation<A>): MatcherResult = value(a).let {
      MatcherResult(
         it is Valid,
         { "$a should be valid, but was: $it" },
         { "$a should not be valid" }
      )
   }
}

infix fun <T> Validation<T>.shouldBeInvalid(value: T) = this should beInvalid(value)

fun <A> beInvalid(a: A) = object : Matcher<Validation<A>> {
   override fun test(value: Validation<A>): MatcherResult = value(a).let {
      MatcherResult(
         it is Invalid,
         { "$a should be invalid" },
         { "$a should not be invalid, but was: $it" }
      )
   }
}

inline fun <T> Validation<T>.shouldBeInvalid(value: T, fn: (Invalid) -> Unit) {
   this.shouldBeInvalid(value)
   fn(this(value) as Invalid)
}

fun Invalid.shouldContainError(field: Any, error: String) {
   val list = this[field]
   list.let {
      it.shouldNotBeNull()
      it shouldContain error
   }
}

fun Invalid.shouldContainError(propertyPaths: Collection<Any>, error: String) {
   val list = this.get(*propertyPaths.toTypedArray())
   list.let {
      it.shouldNotBeNull()
      it shouldContain error
   }
}
