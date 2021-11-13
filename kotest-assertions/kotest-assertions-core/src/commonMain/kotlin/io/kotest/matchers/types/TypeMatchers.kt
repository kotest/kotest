package io.kotest.matchers.types

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import kotlin.reflect.KClass

// alias for beInstanceOf
fun instanceOf(expected: KClass<*>): Matcher<Any?> = beInstanceOf(expected)

inline fun <reified T : Any> instanceOf(): Matcher<Any?> = instanceOf(T::class)

fun beInstanceOf(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
   MatcherResult(
      expected.isInstance(value),
      { "$value is of type ${value::class.print().value} but expected ${expected.print().value}" },
      { "${value::class.print().value} should not be of type ${expected.print().value}" })
}

fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
   override fun test(value: T) = MatcherResult(
      value === ref,
      { "$value should be the same reference as $ref" },
      { "$value should not be the same reference as $ref" })
}

inline fun <U : Any, reified T : U> beInstanceOf2(): Matcher<U> = object : Matcher<U> {

   override fun test(value: U): MatcherResult =
      MatcherResult(
         T::class.isInstance(value),
         { "$value is of type ${value::class.print().value} but expected ${T::class.print().value}" },
         { "$value should not be an instance of ${T::class.print().value}" })
}


// checks that the given value is an instance (of type or of subtype) of T
inline fun <reified T : Any> beInstanceOf(): Matcher<Any?> = beInstanceOf(T::class)

fun beOfType(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
   MatcherResult(
      expected == value::class,
      { "$value should be of type ${expected.print().value}" },
      { "$value should not be of type ${expected.print().value}" })
}

inline fun <reified T : Any> beOfType(): Matcher<Any?> = beOfType(T::class)
