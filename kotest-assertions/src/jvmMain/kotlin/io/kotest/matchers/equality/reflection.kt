package io.kotest.matchers.equality

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * Asserts that this is equal to [other] using specific fields
 *
 * Verifies that [this] instance is equal to [other] using only some specific fields. This is useful for matching
 * on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 * Opposite of [shouldNotBeEqualToUsingFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 *
 * val firstFoo = Foo(1, "Bar!")
 * val secondFoo = Foo(2, "Bar!")
 *
 * firstFoo.shouldBeEqualUsingFields(secondFoo, Foo::description) // Assertion passes
 *
 * firstFoo shouldBe secondFoo // Assertion fails, `equals` is false!
 * ```
 *
 */
fun <T : Any> T.shouldBeEqualToUsingFields(other: T, vararg properties: KProperty<*>) =
    this should beEqualToUsingFields(other, *properties)

/**
 * Asserts that this is NOT equal to [other] using specific fields
 *
 * Verifies that [this] instance is not equal to [other] using only some specific fields. This is useful for matching
 * on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 * Opposite of [shouldBeEqualToUsingFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 *
 * val firstFoo = Foo(1, "Bar!")
 * val secondFoo = Foo(2, "BAT")
 *
 * firstFoo.shouldNotBeEqualToUsingFields(secondFoo, Foo::description) // Assertion passes
 *
 * ```
 *
 * @see [beEqualToUsingFields]
 * @see [shouldNotBeEqualToIgnoringFields]
 *
 */
fun <T : Any> T.shouldNotBeEqualToUsingFields(other: T, vararg properties: KProperty<*>) =
    this shouldNot beEqualToUsingFields(other, *properties)

/**
 * Matcher that compares values using specific fields
 *
 * Verifies that two instances not equal using only some specific fields. This is useful for matching
 * on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 *
 * val firstFoo = Foo(1, "Bar!")
 * val secondFoo = Foo(2, "Bar!")
 *
 * firstFoo should beEqualToUsingFields(secondFoo, Foo::description) // Assertion passes
 *
 * ```
 *
 * @see [shouldBeEqualToUsingFields]
 * @see [shouldNotBeEqualToUsingFields]
 * @see [beEqualToIgnoringFields]
 *
 */
fun <T : Any> beEqualToUsingFields(other: T, vararg fields: KProperty<*>): Matcher<T> = object : Matcher<T> {
  override fun test(value: T): MatcherResult {

    val failed = fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      if (actual == expected) null else {
        "${it.name}: $actual != $expected"
      }
    }

    val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

    return MatcherResult(
        failed.isEmpty(),
        "$value should be equal to $other using fields $fieldsString; Failed for $failed",
        "$value should not be equal to $other using fields $fieldsString"
    )
  }
}

/**
 * Asserts that this is equal to [other] without using specific fields
 *
 * Verifies that [this] instance is equal to [other] without using some specific fields. This is useful for matching
 * on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 * Opposite of [shouldNotBeEqualToIgnoringFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 *
 * val firstFoo = Foo(1, "Bar!")
 * val secondFoo = Foo(2, "Bar!")
 *
 * firstFoo.shouldBeEqualToIgnoringFields(secondFoo, Foo::id) // Assertion passes
 *
 * firstFoo shouldBe secondFoo // Assertion fails, `equals` is false!
 * ```
 *
 */
fun <T : Any> T.shouldBeEqualToIgnoringFields(other: T, vararg properties: KProperty<*>) =
    this should beEqualToIgnoringFields(other, *properties)

/**
 * Asserts that this is not equal to [other] without using specific fields
 *
 * Verifies that [this] instance is not equal to [other] without using some specific fields. This is useful for matching
 * on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 * Opposite of [shouldBeEqualToIgnoringFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 *
 * val firstFoo = Foo(1, "Bar!")
 * val secondFoo = Foo(2, "BAT!")
 *
 * firstFoo.shouldNotBeEqualToIgnoringFields(secondFoo, Foo::id) // Assertion passes
 * ```
 *
 */
fun <T : Any> T.shouldNotBeEqualToIgnoringFields(other: T, vararg properties: KProperty<*>) =
    this shouldNot beEqualToIgnoringFields(other, *properties)

/**
 * Matcher that compares values without using specific fields
 *
 * Verifies that two instances are equal by not using only some specific fields. This is useful for matching
 * on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 *
 * val firstFoo = Foo(1, "Bar!")
 * val secondFoo = Foo(2, "Bar!")
 *
 * firstFoo should beEqualToIgnoringFields(secondFoo, Foo::id) // Assertion passes
 *
 * ```
 *
 * @see [beEqualToUsingFields]
 * @see [shouldBeEqualToIgnoringFields]
 * @see [shouldNotBeEqualToIgnoringFields]
 *
 */
fun <T : Any> beEqualToIgnoringFields(other: T,
                                      vararg fields: KProperty<*>): Matcher<T> = object : Matcher<T> {
  override fun test(value: T): MatcherResult {

    val fieldNames = fields.map { it.name }
    val failed = value::class.memberProperties.filterNot { fieldNames.contains(it.name) }.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      if (actual == expected) null else {
        "${it.name}: $actual != $expected"
      }
    }

    val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

    return MatcherResult(
        failed.isEmpty(),
        "$value should be equal to $other ignoring fields $fieldsString; Failed for $failed",
        "$value should not be equal to $other ignoring fields $fieldsString"
    )
  }
}
