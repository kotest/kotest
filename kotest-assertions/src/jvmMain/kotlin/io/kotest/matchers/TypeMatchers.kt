package io.kotest.matchers

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.neverNullMatcher
import kotlin.reflect.KClass

// alias for beInstanceOf
fun instanceOf(expected: KClass<*>): Matcher<Any?> = beInstanceOf(expected)

fun beInstanceOf(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
  MatcherResult(
      expected.java.isAssignableFrom(value.javaClass),
      "$value is of type ${value.javaClass} but expected $expected",
      "$value should not be of type $expected"
  )
}

fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
  override fun test(value: T) = MatcherResult(value === ref, "$value should be the same reference as $ref", "$value should not be the same reference as $ref")
}

inline fun <U : Any, reified T : U> beInstanceOf2(): Matcher<U> = object : Matcher<U> {

  override fun test(value: U): MatcherResult =
      MatcherResult(
          T::class.java.isAssignableFrom(value.javaClass),
          "$value is of type ${value.javaClass} but expected ${T::class.java.canonicalName}",
          "$value should not be an instance of ${T::class.java.canonicalName}")

}


// checks that the given value is an instance (of type or of subtype) of T
inline fun <reified T : Any> beInstanceOf(): Matcher<Any?> = beInstanceOf(T::class)

fun beOfType(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
  MatcherResult(
      value.javaClass == expected.java,
      "$value should be of type ${expected.qualifiedName}",
      "$value should not be of type ${expected.qualifiedName}")
}

inline fun <reified T : Any> beOfType(): Matcher<Any?> = beOfType(T::class)
