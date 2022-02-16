package io.kotest.matchers.equality

import io.kotest.assertions.eq.eq
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

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
 * Note: Throws [IllegalArgumentException] if [properties] contains any non public property
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
 * Note: Throws [IllegalArgumentException] if [properties] contains any non public property
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
         { "$value should be equal to $other using fields $fieldsString; Failed for $failed" },
         {
            "$value should not be equal to $other using fields $fieldsString"
         })
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
fun <T : Any> T.shouldBeEqualToIgnoringFields(other: T, property: KProperty<*>, vararg others: KProperty<*>) {
   this should beEqualToIgnoringFields(other = other, ignorePrivateFields = true, property = property, others = others)
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
 */

fun <T : Any> T.shouldBeEqualToIgnoringFields(
   other: T,
   ignorePrivateFields: Boolean,
   property: KProperty<*>,
   vararg others: KProperty<*>,
) {
   this should beEqualToIgnoringFields(
      other = other,
      ignorePrivateFields = ignorePrivateFields,
      property = property,
      others = others
   )
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
fun <T : Any> T.shouldNotBeEqualToIgnoringFields(other: T, property: KProperty<*>, vararg others: KProperty<*>) =
   this shouldNot beEqualToIgnoringFields(
      other = other,
      ignorePrivateFields = true,
      property = property,
      others = others,
   )


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
fun <T : Any> T.shouldNotBeEqualToIgnoringFields(
   other: T,
   ignorePrivateFields: Boolean,
   property: KProperty<*>,
   vararg others: KProperty<*>
) =
   this shouldNot beEqualToIgnoringFields(
      other = other,
      ignorePrivateFields = ignorePrivateFields,
      property = property,
      others = others,
   )

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
   property: KProperty<*>,
   vararg others: KProperty<*>
): Matcher<T> = object : Matcher<T> {

   override fun test(value: T): MatcherResult {
      val fields = listOf(property) + others
      val fieldNames = fields.map { it.name }
      val fieldsExcludingGivenFields = value::class.memberProperties
         .filterNot { fieldNames.contains(it.name) }

      val fieldsToBeConsidered: List<KProperty<*>> = if (ignorePrivateFields) {
         fieldsExcludingGivenFields.filter { it.visibility == KVisibility.PUBLIC }
      } else {
         fieldsExcludingGivenFields.onEach { it.isAccessible = true }
      }

      val failed = checkEqualityOfFields(fieldsToBeConsidered, value, other)
      val fieldsString = fields.joinToString(", ", "[", "]") { it.name }

      return MatcherResult(
         failed.isEmpty(),
         { "$value should be equal to $other ignoring fields $fieldsString; Failed for $failed" },
         { "$value should not be equal to $other ignoring fields $fieldsString" }
      )
   }
}


fun <T : Any> T.shouldBeEqualToComparingFields(
   other: T,
   ignorePrivateFields: Boolean = true,
   ignoreComputedFields: Boolean = true
) {
   this should beEqualComparingFields(other, ignorePrivateFields, emptyList(), ignoreComputedFields)
}

fun <T : Any> T.shouldBeEqualToComparingFieldsExcept(
   other: T,
   ignorePrivateFields: Boolean,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>,
   ignoreComputedFields: Boolean = true
) {
   this should beEqualComparingFields(
      other,
      ignorePrivateFields,
      listOf(ignoreProperty) + ignoreProperties,
      ignoreComputedFields
   )
}

fun <T : Any> T.shouldNotBeEqualToComparingFieldsExcept(
   other: T,
   ignorePrivateFields: Boolean,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>,
   includeComputedProperties: Boolean = false
) {
   this shouldNot beEqualComparingFields(
      other,
      ignorePrivateFields,
      listOf(ignoreProperty) + ignoreProperties,
      includeComputedProperties
   )
}

fun <T : Any> T.shouldBeEqualToComparingFieldsExcept(
   other: T,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>
) {
   this should beEqualComparingFields(other, true, listOf(ignoreProperty) + ignoreProperties, true)
}

fun <T : Any> T.shouldNotBeEqualToComparingFieldsExcept(
   other: T,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>
) {
   this should beEqualComparingFields(other, true, listOf(ignoreProperty) + ignoreProperties, true)
}

infix fun <T : Any> T.shouldBeEqualToComparingFields(other: T) {
   this.shouldBeEqualToComparingFields(other, true)
}

infix fun <T : Any> T.shouldNotBeEqualToComparingFields(other: T) {
   this shouldNot beEqualComparingFields(other, true, emptyList(), true)
}

fun <T : Any> T.shouldNotBeEqualToComparingFields(
   other: T,
   ignorePrivateFields: Boolean = true,
   ignoreComputedFields: Boolean = true
) {
   this shouldNot beEqualComparingFields(other, ignorePrivateFields, emptyList(), ignoreComputedFields)
}

private typealias PropertyPredicate = (KProperty<*>) -> Boolean

// If no java field exists, it is a computed property which only has a getter
private val nonComputed: PropertyPredicate = { it.javaField != null }
private val nonPrivate: PropertyPredicate = { it.visibility != KVisibility.PRIVATE }

private infix fun PropertyPredicate.and(other: PropertyPredicate) =
   { property: KProperty<*> -> this(property) && other(property) }

fun <T : Any> beEqualComparingFields(
   other: T,
   ignorePrivateFields: Boolean,
   propertiesToExclude: List<KProperty<*>>,
   ignoreComputedFields: Boolean,
) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val (failed, fieldsToCompare) = checkEqualityOfFieldsRecursively(
         value,
         other,
         ignorePrivateFields,
         ignoreComputedFields,
         propertiesToExclude
      )

      return MatcherResult(
         failed.isEmpty(),
         {
            """Expected ${value.print().value} to equal ${other.print().value}
            | Using fields: ${fieldsToCompare.joinToString(", ") { it.name }}
            | Value differ at:
            | ${failed.withIndex().joinToString("\n") { "${it.index + 1}) ${it.value}" }}
         """.trimMargin()
         },
         {
            """Expected ${value.print().value} to not equal ${other.print().value}
            | Using fields: ${fieldsToCompare.joinToString(", ") { it.name }}
         """.trimMargin()
         }
      )
   }
}

private fun <T> checkEqualityOfFields(fields: List<KProperty<*>>, value: T, other: T): List<String> {
   return fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)

      val isEqual = eq(actual, expected) == null

      if (isEqual) null else "${it.name}: ${actual.print().value} != ${expected.print().value}"
   }
}

private fun <T> checkEqualityOfFieldsRecursively(
   value: T,
   other: T,
   ignorePrivateFields: Boolean,
   ignoreComputedFields: Boolean,
   propertiesToExclude: List<KProperty<*>>,
   level: Int = 1
): Pair<List<String>, List<KProperty1<out T, *>>> {
   val predicates = listOfNotNull(
      if (ignorePrivateFields) nonPrivate else null,
      if (ignoreComputedFields) nonComputed else null,
      { it !in propertiesToExclude }
   ).reduce { a, b -> a and b }

   val fields = value!!::class.memberProperties
      .asSequence()
      .onEach { it.isAccessible = true }
      .filter(predicates)
      .sortedBy { it.name }
      .toList()

   return fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      val typeName = it.returnType.toString()

      if (typeName.startsWith("kotlin") || typeName.startsWith("java")) {
         val throwable = eq(actual, expected)
         if (throwable != null) {
            val heading = it.name
            "$heading\n${"\t".repeat(level + 1)}${throwable.message}"
         } else {
            null
         }
      } else {
         val (errorMessage, _) = checkEqualityOfFieldsRecursively(
            actual,
            expected,
            ignorePrivateFields,
            ignoreComputedFields,
            propertiesToExclude,
            level + 1
         )
         if (errorMessage.isEmpty()) {
            null
         } else {
            val innerErrorMessage = errorMessage.joinToString("\n") { msg -> "\t".repeat(level + 1) + msg }
            val errorHeading = it.name
            "$errorHeading${"\t".repeat(level)}\n$innerErrorMessage"
         }
      }
   } to fields
}

