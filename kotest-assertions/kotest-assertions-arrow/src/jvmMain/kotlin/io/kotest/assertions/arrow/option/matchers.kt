package io.kotest.assertions.arrow.option

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Option<*>.shouldBeSome() {
  contract {
    returns() implies (this@shouldBeSome is Some<*>)
  }
  this should beSome()
}

fun beSome() = object : Matcher<Option<*>> {
  override fun test(value: Option<*>): MatcherResult =
      MatcherResult(value is Some, "$value should be Some", "$value should not be Some")
}

infix fun <T> Option<T>.shouldBeSome(t: T) = this should beSome(t)
infix fun <T> Option<T>.shouldNotBeSome(t: T) = this shouldNot beSome(t)
fun <T> beSome(t: T) = object : Matcher<Option<T>> {
  override fun test(value: Option<T>): MatcherResult {
    return when (value) {
      is None -> {
        MatcherResult(false, "Option should be Some($t) but was None", "")
      }
      is Some -> {
        if (value.t == t)
          MatcherResult(true, "Option should be Some($t)", "Option should not be Some($t)")
        else
          MatcherResult(false, "Option should be Some($t) but was Some(${value.t})", "")
      }
    }
  }
}

infix fun <T> Option<T>.shouldBeSome(fn: (T) -> Unit) {
  this.shouldBeSome()
  fn((this.t as T))
}

fun Option<Any>.shouldBeNone() = this should beNone()
fun Option<Any>.shouldNotBeNone() = this shouldNot beNone()
fun beNone() = object : Matcher<Option<Any>> {
  override fun test(value: Option<Any>): MatcherResult {
    return when (value) {
      is None -> {
        MatcherResult(true, "Option should be None", "Option should not be None")
      }
      is Some -> {
        MatcherResult(false, "Option should be None but was Some(${value.t})", "")
      }
    }
  }
}
