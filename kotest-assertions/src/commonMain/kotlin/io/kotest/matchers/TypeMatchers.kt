package io.kotest.matchers

import kotlin.reflect.KClass

// alias for beInstanceOf
fun instanceOf(expected: KClass<*>): Matcher<Any?> = beInstanceOf(expected)

fun beInstanceOf(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
  MatcherResult(
     expected.isInstance(value),
      "$value is of type ${value::class.qualifiedName} but expected ${expected.qualifiedName}",
      "${value::class.qualifiedName} should not be of type ${expected.qualifiedName}"
  )
}

fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
  override fun test(value: T) = MatcherResult(value === ref, "$value should be the same reference as $ref", "$value should not be the same reference as $ref")
}

inline fun <U : Any, reified T : U> beInstanceOf2(): Matcher<U> = object : Matcher<U> {

  override fun test(value: U): MatcherResult =
      MatcherResult(
          T::class.isInstance(value),
          "$value is of type ${value::class.qualifiedName} but expected ${T::class.qualifiedName}",
          "$value should not be an instance of ${T::class.qualifiedName}")

}


// checks that the given value is an instance (of type or of subtype) of T
inline fun <reified T : Any> beInstanceOf(): Matcher<Any?> = beInstanceOf(T::class)

fun beOfType(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
  MatcherResult(
      expected == value::class,
      "$value should be of type ${expected.qualifiedName}",
      "$value should not be of type ${expected.qualifiedName}")
}

inline fun <reified T : Any> beOfType(): Matcher<Any?> = beOfType(T::class)
