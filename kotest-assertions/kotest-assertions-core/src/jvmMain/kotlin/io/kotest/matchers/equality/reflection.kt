package io.kotest.matchers.equality

import io.kotest.assertions.eq.EqCompare
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Asserts that this [T] is equal to [other] : [T] using specific fields
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
 * firstFoo.shouldBeEqualToUsingFields(secondFoo, Foo::description) // Assertion passes
 *
 * firstFoo shouldBe secondFoo // Assertion fails, `equals` is false!
 * ```
 *
 * Note: Throws [IllegalArgumentException] if [properties] contains any non-public property
 * or if [other] is not an instance of the same class as receiver [T]
 *
 */
fun <T : Any> T.shouldBeEqualToUsingFields(other: T, vararg properties: KProperty<*>) {
   require(this::class.isInstance(other)){"object is not an instance of declaring class"}
   this should beEqualToUsingFields(other, *properties)
}

/**
 * Asserts that this [T] is equal to [other] : [V] using specific fields
 *
 * Verifies that [this] instance is equal to [other] using only some specific fields. This is useful in scenarios where
 * you want to compare objects that share common properties but are not of the same class,
 * such as comparing a domain model with a DTO.
 *
 * Opposite of [shouldNotBeEqualToUsingFields]
 *
 * Example:
 * ```
 * data class Foo(val id: Int, val description: String)
 * data class Fuu(val id: Int, val description: String, val isActive: Boolean)
 *
 * val foo = Foo(1, "Bar!")
 * val fuu = Fuu(2, "Bar!", false)
 *
 * foo.shouldBeEqualToUsingFields(fuu, Foo::description) // Assertion passes
 *
 * foo shouldBe fuu // Assertion fails, `equals` is false!
 * ```
 *
 * Note: Throws [IllegalArgumentException] if [properties] contains any non-public property
 *
 */
fun <T : Any, V: Any> T.shouldBeEqualToDifferentTypeUsingFields(other: V, vararg properties: KProperty1<T, *>) {
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
fun <T : Any, V: Any> beEqualToUsingFields(other: V, vararg fields: KProperty<*>): Matcher<T> = object : Matcher<T> {
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

/**
 *  Matcher that compares values without using field by field comparison.
 *
 * This matcher should be used to check equality of two class for which you want to consider their fields for equality
 * instead of its `equals` method.
 *
 * This matcher recursively check equality of given values till we get a java class, kotlin class or fields for which we have
 * specified to use default shouldBe. Once we get a java class, kotlin class or specified field the equality of that fields
 * will be same as that we get with shouldBe matcher.
 *
 * @param other the other class to which equality need to be checked.
 * @param fieldsEqualityCheckConfig the config to control the field by field comparison.
 *
 * @see FieldsEqualityCheckConfig
 *
 * Example:
 *  ```
 *  package org.foo.bar.domain
 *
 *  class ANestedClass(val name: String, val nestedField: AnotherNestedClass) {
 *    private val id = UUID.randomUUID()
 *  }
 *  class AnotherNestedClass(val buffer: Buffer) {
 *    val aComputedField: Int
 *      get() = Random.nextInt()
 *  }
 *  class SomeClass(val name: String, val randomId: UUID ,val nestedField: ANestedClass)
 *
 *
 *  someClass.shouldBeEqualToComparingFields(anotherInstanceOfSomeClass, FieldsEqualityCheckConfig(
 *    ignorePrivateFields = true,
 *    ignoreComputedFields = true,
 *    propertiesToExclude = listOf(SomeClass::randomId),
 *    useDefaultShouldBeForFields = listOf("org.foo.bar.domain.AnotherNestedClass")
 *  ))
 *  ```
 * */
fun <T : Any> T.shouldBeEqualToComparingFields(
   other: T,
   fieldsEqualityCheckConfig: FieldsEqualityCheckConfig = FieldsEqualityCheckConfig()
) {
   this should beEqualComparingFields(other, fieldsEqualityCheckConfig)
}

infix fun <T : Any> T.shouldBeEqualToComparingFields(other: T) {
   shouldBeEqualToComparingFields(other, FieldsEqualityCheckConfig())
}

infix fun <T : Any> T.shouldNotBeEqualToComparingFields(other: T) {
   this shouldNot beEqualComparingFields(other, FieldsEqualityCheckConfig())
}

fun <T : Any> T.shouldNotBeEqualToComparingFields(
   other: T,
   fieldsEqualityCheckConfig: FieldsEqualityCheckConfig
) {
   this shouldNot beEqualComparingFields(other, fieldsEqualityCheckConfig)
}

fun <T : Any> beEqualComparingFields(
   other: T,
   fieldsEqualityCheckConfig: FieldsEqualityCheckConfig
): Matcher<T> {
   return object : Matcher<T> {
      override fun test(value: T): MatcherResult {
         val (failed, fieldsToCompare) = checkEqualityOfFieldsRecursively(
            value,
            other,
            fieldsEqualityCheckConfig,
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
}

private fun <T, V> checkEqualityOfFields(fields: List<KProperty<*>>, value: T, other: V): List<String> {
   requireNotNull(value)
   requireNotNull(other)
   val equalityChecker: (actual: Any?, expected: Any?, propertyName: String) -> String? =
      { actual, expected, propertyName ->
         val isEqual = EqCompare.compare(actual, expected, false) == null
         if (isEqual) null else "$propertyName: ${actual.print().value} != ${expected.print().value}"
      }
   return when (value::class == other::class) {
      true -> fields.mapNotNull {
         val actual = it.getter.call(value)
         val expected = it.getter.call(other)
         equalityChecker(actual, expected, it.name)
      }

      false -> {
         val otherPropertiesByName = other::class.memberProperties.associateBy { it.name }
         return fields.mapNotNull { tProperty ->
            val actual = tProperty.getter.call(value)
            val propOfV = otherPropertiesByName[tProperty.name]

            if (propOfV == null) {
               "${tProperty.name}: property not found in ${other::class.simpleName}"
            } else {
               val expected = propOfV.getter.call(other)
               equalityChecker(actual, expected, tProperty.name)
            }
         }
      }
   }
}

internal fun <T> checkEqualityOfFieldsRecursively(
   value: T,
   other: T,
   config: FieldsEqualityCheckConfig,
   level: Int = 1
): Pair<List<String>, List<KProperty1<out T, *>>> {
   val predicates: (KProperty<*>) -> Boolean = listOfNotNull(
      if (config.ignorePrivateFields) notPrivate else null,
      if (config.ignoreComputedFields) notComputed else null,
      { it !in config.propertiesToExclude }
   ).reduce { a, b -> a and b }

   val fields: List<KProperty1<out T & Any, *>> = value!!::class.memberProperties
      .asSequence()
      .onEach { it.isAccessible = true }
      .filter(predicates)
      .sortedBy { it.name }
      .toList()

   return fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)
      val heading = "${it.name}:"

      when (comparisonToUse(actual, expected, config.useDefaultShouldBeForFields)) {
         FieldComparison.RECURSIVE -> {
            val (errorMessage, _) = checkEqualityOfFieldsRecursively(
               actual,
               expected,
               config,
               level + 1
            )
            if (errorMessage.isEmpty()) {
               null
            } else {
               val innerErrorMessage = errorMessage.joinToString("\n") { msg -> "\t".repeat(level + 1) + msg }
               "$heading${"\t".repeat(level)}\n$innerErrorMessage"
            }
         }

         else -> {
            val throwable = EqCompare.compare(actual, expected, false)
            if (throwable != null) {
               "$heading\n${"\t".repeat(level + 1)}${throwable.message}"
            } else {
               null
            }
         }
      }

   } to fields
}

internal fun comparisonToUse(
   actual: Any?,
   expected: Any?,
   useDefaultEqualForFields: List<String>
): FieldComparison = when {
   actual == null || expected == null -> FieldComparison.DEFAULT
   isEnum(expected) || isEnum(actual) -> FieldComparison.DEFAULT
   (expected is List<*> && actual is List<*>) -> FieldComparison.LIST
   (expected is Map<*, *> && actual is Map<*, *>) -> FieldComparison.MAP
   (expected is Set<*> && actual is Set<*>) -> FieldComparison.SET
   (isArray(expected) && isArray(actual)) -> FieldComparison.ARRAY
   typeIsJavaOrKotlinBuiltIn(expected) || typeIsJavaOrKotlinBuiltIn(actual) -> FieldComparison.DEFAULT
   useDefaultEqualForFields.contains(expected::class.java.canonicalName) ||
      useDefaultEqualForFields.contains(actual::class.java.canonicalName) -> FieldComparison.DEFAULT

   actual::class != expected::class -> FieldComparison.DEFAULT
   else -> FieldComparison.RECURSIVE
}

internal fun isArray(value: Any?) = when (value) {
   null -> false
   is Array<*> -> true
   value::class.java.isArray -> true
   is ByteArray -> true
   is ShortArray -> true
   is IntArray -> true
   is LongArray -> true
   is FloatArray -> true
   is DoubleArray -> true
   is CharArray -> true
   is BooleanArray -> true
   else -> false
}

internal fun isEnum(value: Any?) = when (value) {
   null -> false
   is Enum<*> -> true
   value::class.java.isEnum -> true
   else -> false
}

internal fun typeIsJavaOrKotlinBuiltIn(value: Any): Boolean {
   val typeName = value::class.java.canonicalName ?: return false
   return typeName.startsWith("kotlin.") || typeName.startsWith("java.")
}

internal enum class FieldComparison { DEFAULT, RECURSIVE, LIST, MAP, SET, ARRAY }

/**
 * A config for controlling the way shouldBeEqualToComparingFields compare fields.
 *
 * @property ignorePrivateFields specify whether to exclude private fields in comparison. Default true.
 * @property ignoreComputedFields specify whether to exclude computed fields in comparison. Default true.
 * @property propertiesToExclude specify which field to exclude in comparison. Default emptyList.
 * @property useDefaultShouldBeForFields fully qualified name of data type for which we need to use default shouldBe
 *                                       matcher instead of recursive field by field comparison.
 * */
data class FieldsEqualityCheckConfig(
   val ignorePrivateFields: Boolean = true,
   val ignoreComputedFields: Boolean = true,
   val propertiesToExclude: List<KProperty<*>> = emptyList(),
   val useDefaultShouldBeForFields: List<String> = emptyList()
)


