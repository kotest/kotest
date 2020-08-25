package io.kotest.assertions.arrow.either

import arrow.core.Either
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.types.beInstanceOf2
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@UseExperimental(ExperimentalContracts::class)
fun <T> Either<*, T>.shouldBeRight() {
  contract {
    returns() implies (this@shouldBeRight is Either.Right<*>)
  }
  this should beRight()
}

fun <T> Either<Any?, T>.shouldNotBeRight() = this shouldNot beRight()
fun <T> beRight() =   beInstanceOf2<Either<Any?, T>, Either.Right<T>>()

inline infix fun <B> Either<*, B>.shouldBeRight(fn: (B) -> Unit) {
  this should beRight()
  fn((this as Either.Right<B>).b)
}

infix fun <B> Either<Any?, B>.shouldBeRight(b: B) = this should beRight(b)
infix fun <B> Either<Any?, B>.shouldNotBeRight(b: B) = this shouldNot beRight(b)
fun <B> beRight(b: B) = object : Matcher<Either<Any?, B>> {
  override fun test(value: Either<Any?, B>): MatcherResult {
    return when (value) {
      is Either.Left -> {
        MatcherResult(false, "Either should be Right($b) but was Left(${value.a})", "Either should not be Right($b)")
      }
      is Either.Right -> {
        if (value.b == b)
          MatcherResult(true, "Either should be Right($b)", "Either should not be Right($b)")
        else
          MatcherResult(false, "Either should be Right($b) but was Right(${value.b})", "Either should not be Right($b)")
      }
    }
  }
}

@UseExperimental(ExperimentalContracts::class)
fun <T> Either<T, Any?>.shouldBeLeft() {
  contract {
    returns() implies (this@shouldBeLeft is Either.Left<*>)
  }
  this should beLeft()
}

fun <T> Either<T, Any?>.shouldNotBeLeft() = this shouldNot beLeft()
fun <T> beLeft() = beInstanceOf2<Either<T, Any?>, Either.Left<T>>()

inline infix fun <A> Either<A, *>.shouldBeLeft(fn: (A) -> Unit) {
  this should beLeft()
  fn((this as Either.Left<A>).a)
}


infix fun <A> Either<A, Any?>.shouldBeLeft(a: A) = this should beLeft(a)
infix fun <A> Either<A, Any?>.shouldNotBeLeft(a: A) = this shouldNot beLeft(a)
fun <A> beLeft(a: A) = object : Matcher<Either<A, Any?>> {
  override fun test(value: Either<A, Any?>): MatcherResult {
    return when (value) {
      is Either.Right -> {
        MatcherResult(false, "Either should be Left($a) but was Right(${value.b})", "Either should not be Right($a)")
      }
      is Either.Left -> {
        if (value.a == a)
          MatcherResult(true, "Either should be Left($a)", "Either should not be Left($a)")
        else
          MatcherResult(false, "Either should be Left($a) but was Left(${value.a})", "Either should not be Right($a)")
      }
    }
  }
}

inline fun <reified A> Either<Any?, Any?>.shouldBeLeftOfType() = this should beLeftOfType<A>()
inline fun <reified A> Either<Any?, Any?>.shouldNotBeLeftOfType() = this shouldNot beLeftOfType<A>()
inline fun <reified A> beLeftOfType() = object : Matcher<Either<Any?, Any?>> {
  override fun test(value: Either<Any?, Any?>): MatcherResult {
    return when (value) {
      is Either.Right -> {
        MatcherResult(false, "Either should be Left<${A::class.qualifiedName}> but was Right(${value.b})", "")
      }
      is Either.Left -> {
        val valueA = value.a
        if (valueA is A)
          MatcherResult(true, "Either should be Left<${A::class.qualifiedName}>", "Either should not be Left")
        else
          MatcherResult(false,
              "Either should be Left<${A::class.qualifiedName}> but was Left<${if (valueA == null) "Null" else valueA::class.qualifiedName}>",
              "Either should not be Left")
      }
    }
  }
}
