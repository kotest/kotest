package io.kotest.matchers.types

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import kotlin.reflect.KClass


/**
 * Asserts that a value is an instance of the specified reified type.
 *
 * This is a reified shortcut to [instanceOf], allowing type-safe matching without needing a KClass reference.
 *
 * ```
 * "kotest" shouldBe instanceOf<String>()   // Assertion passes
 * 123 shouldBe instanceOf<String>()        // Assertion fails
 * ```
 *
 * @see beInstanceOf
 */
inline fun <reified T : Any> instanceOf(): Matcher<Any?> = instanceOf(T::class)

/**
 * Reified version of [beInstanceOf] for convenience.
 *
 * Uses a reified type parameter to infer the class to check against.
 *
 * ```
 * val obj: Any = 42
 * obj should beInstanceOf<Int>()      // Assertion passes
 * obj shouldNot beInstanceOf<String>() // Assertion passes
 * ```
 *
 * @see beInstanceOf
 */
inline fun <reified T : Any> beInstanceOf(): Matcher<Any?> = instanceOf<T>()



/**
 * Asserts that a value is an instance of the given class.
 *
 * Verifies that the subject is an instance of the given [expected] class.
 * This matcher will fail if the value is null or is not an instance of [expected].
 *
 * ```
 * "hello" shouldBe instanceOf(String::class) // Assertion passes
 * 123 shouldBe instanceOf(String::class)     // Assertion fails
 * ```
 *
 * @see beInstanceOf
 */
fun instanceOf(expected: KClass<*>): Matcher<Any?> = beInstanceOf(expected)

/**
 * Matcher that asserts a value is an instance of the specified class.
 *
 * Validates that the subject is non-null and is an instance of the given [expected] class.
 *
 * ```
 * val obj: Any = "sample"
 * obj should beInstanceOf(String::class)    // Assertion passes
 * obj shouldNot beInstanceOf(Int::class)    // Assertion passes
 * ```
 *
 * @see instanceOf
 */
fun beInstanceOf(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
   MatcherResult(
      expected.isInstance(value),
      { "$value is of type ${value::class.print().value} but expected ${expected.print().value}" },
      { "${value::class.print().value} should not be of type ${expected.print().value}" })
}

/**
 * Asserts that the subject is the same reference as [ref].
 *
 * Verifies that the subject and [ref] are the exact same object in memory.
 *
 * ```
 * val a = "hello"
 * val b = a
 * val c = "hello"
 *
 * a should beTheSameInstanceAs(b)    // Assertion passes
 * a shouldNot beTheSameInstanceAs(c) // Assertion passes
 * ```
 */
fun <T> beTheSameInstanceAs(ref: T): Matcher<T> = object : Matcher<T> {
   override fun test(value: T) = MatcherResult(
      value === ref,
      { "$value should be the same reference as $ref" },
      { "$value should not be the same reference as $ref" })
}

/**
 * Reified variant of [beOfType] for concise usage.
 *
 * Uses the reified type parameter to determine the expected type.
 *
 * ```
 * val x: Number = 42
 * x should beOfType<Int>()       // Assertion passes
 * x shouldNot beOfType<Number>() // Assertion fails, x is Int, not exactly Number
 * ```
 */
inline fun <reified T : Any> beOfType(): Matcher<Any?> = beOfType(T::class)

/**
 * Asserts that the subject has exactly the specified type.
 *
 * Verifies that the class of the subject is exactly equal to [expected], not just assignable from it.
 *
 * ```
 * val x: Number = 42
 * x shouldNot beOfType(Number::class) // Assertion fails because x is Int
 * x should beOfType(Int::class)       // Assertion passes
 * ```
 *
 * @see beInstanceOf
 */
fun beOfType(expected: KClass<*>): Matcher<Any?> = neverNullMatcher { value ->
   MatcherResult(
      expected == value::class,
      { "$value should be of type ${expected.print().value}" },
      { "$value should not be of type ${expected.print().value}" })
}
