package io.kotest.matchers.equality

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

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
 * Note:
 * 1) Throws [IllegalArgumentException] in case [properties] parameter is not provided.
 * 2) Throws [IllegalArgumentException] if [properties] contains any non public property
 *
 */
fun <T : Any> T.shouldBeEqualToUsingFields(other: T, vararg properties: KProperty<*>) {
   this should beEqualToUsingFields(other, *properties)
}

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
 * Note:
 * 1) Throws [IllegalArgumentException] in case [properties] parameter is not provided.
 * 2) Throws [IllegalArgumentException] if [properties] contains any non public property
 *
 *
 * @see [beEqualToUsingFields]
 * @see [shouldNotBeEqualToIgnoringFields]
 *
 */
fun <T : Any> T.shouldNotBeEqualToUsingFields(other: T, vararg properties: KProperty<*>) {
   this shouldNot beEqualToUsingFields(other, *properties)
}

/**
 * Matcher that compares values using specific fields
 *
 * Verifies that two instances are equal considering only some specific fields. This is useful for matching on objects
 * that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it doesn't
 * matter for you, for example). However, if no fields are specified, all public fields are considered.
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
 * Note: Throws [IllegalArgumentException] if [fields] contains any non public property
 *
 * @see [shouldBeEqualToUsingFields]
 * @see [shouldNotBeEqualToUsingFields]
 * @see [beEqualToIgnoringFields]
 *
 */
fun <T : Any> beEqualToUsingFields(other: T, vararg fields: KProperty<*>): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val hasNonPublicFields = fields.any { it.visibility != KVisibility.PUBLIC }
      if (hasNonPublicFields) {
         throw IllegalArgumentException("Only fields of public visibility are allowed to be use for used for checking equality")
      }
      val fieldsToBeConsidered: List<KProperty<*>> = fields.toList().takeUnless { it.isEmpty() }
         ?: value::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
      val failed = checkEqualityOfFields(fieldsToBeConsidered, value, other)
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
 * Note: Throws [IllegalArgumentException] in case [properties] parameter is not provided.
 */
fun <T : Any> T.shouldBeEqualToIgnoringFields(other: T, vararg properties: KProperty<*>) {
   this should beEqualToIgnoringFields(other = other, ignorePrivateFields = true, fields = *properties)
}

/**
 * Asserts that this is equal to [other] without using specific fields
 *
 * Verifies that [this] instance is equal to [other] without using some specific fields and ignoring/not-ignoring
 * private fields.
 * This is useful for matching on objects that contain unknown values, such as a database Entity that contains an
 * ID (you don't know this ID, and it doesn't matter for you, for example)
 *
 * Opposite of [shouldNotBeEqualToIgnoringFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String, private val quote: String)
 *
 * val firstFoo = Foo(1, "Bar!", "Q1")
 * val secondFoo = Foo(2, "Bar!", "Q2")
 *
 * firstFoo.shouldBeEqualToIgnoringFields(other = secondFoo, ignorePrivateFields = true , properties = Foo::id) // Assertion passes
 * firstFoo.shouldBeEqualToIgnoringFields(other = secondFoo, ignorePrivateFields = false , properties = Foo::id) // Assertion fails
 *
 * firstFoo shouldBe secondFoo // Assertion fails, `equals` is false!
 * ```
 *
 * Note: Throws [IllegalArgumentException] in case [properties] parameter is not provided.
 */

fun <T : Any> T.shouldBeEqualToIgnoringFields(other: T, ignorePrivateFields: Boolean, vararg properties: KProperty<*>) {
   this should beEqualToIgnoringFields(other = other, ignorePrivateFields = ignorePrivateFields, fields = *properties)
}

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
   this shouldNot beEqualToIgnoringFields(other = other, ignorePrivateFields = true, fields = *properties)


/**
 * Asserts that this is not equal to [other] without using specific fields
 *
 * Verifies that [this] instance is not equal to [other] without using some specific fields and ignoring/not-ignoring
 * private fields.
 * This is useful for matching on objects that contain unknown values, such as a database Entity that contains an ID (you don't know this ID, and it
 * doesn't matter for you, for example)
 *
 * Opposite of [shouldBeEqualToIgnoringFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String, private val quote: String)
 *
 * val firstFoo = Foo(1, "Bar!", "Q1")
 * val secondFoo = Foo(2, "Bar!", "Q2")
 *
 * firstFoo.shouldNotBeEqualToIgnoringFields(other = secondFoo, ignorePrivateFields = false, properties = Foo::id) // Assertion passes
 * firstFoo.shouldNotBeEqualToIgnoringFields(other = secondFoo, ignorePrivateFields = true, properties = Foo::id) // Assertion fails
 * ```
 *
 */
fun <T : Any> T.shouldNotBeEqualToIgnoringFields(other: T, ignorePrivateFields: Boolean, vararg properties: KProperty<*>) =
   this shouldNot beEqualToIgnoringFields(other = other, ignorePrivateFields = ignorePrivateFields, fields = *properties)

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
fun <T : Any> beEqualToIgnoringFields(
   other: T,
   ignorePrivateFields: Boolean,
   vararg fields: KProperty<*>
): Matcher<T> = object : Matcher<T> {
   init {
      require(fields.isNotEmpty()) { "At-least one field must be ignored when checking for equality" }
   }

   override fun test(value: T): MatcherResult {
      val fieldNames = fields.map { it.name }
      val fieldsExcludingGivenFields = value::class.memberProperties
         .filterNot { fieldNames.contains(it.name) }

      val fieldsToBeConsidered: List<KProperty<*>> = if(ignorePrivateFields) {
         fieldsExcludingGivenFields.filter { it.visibility == KVisibility.PUBLIC }
      } else {
         fieldsExcludingGivenFields.onEach { it.isAccessible = true }
      }

      val failed = checkEqualityOfFields(fieldsToBeConsidered, value, other)
      val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

      return MatcherResult(
         failed.isEmpty(),
         "$value should be equal to $other ignoring fields $fieldsString; Failed for $failed",
         "$value should not be equal to $other ignoring fields $fieldsString"
      )
   }
}

private fun <T> checkEqualityOfFields(fields: List<KProperty<*>>, value: T, other: T): List<String> {
   return fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      if (actual == expected) null else {
         "${it.name}: $actual != $expected"
      }
   }
}
