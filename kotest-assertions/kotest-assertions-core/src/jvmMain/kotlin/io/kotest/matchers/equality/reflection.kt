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

/**
 * Matcher that compares values without using field by field comparison.
 *
 * This matcher should be used to check equality of two class for which you want to consider their fields for equality
 * instead of its `equals` method.
 *
 * This matcher recursively check equality of given values till we get a java class, kotlin class or fields for which we have
 * specified to use default shouldBe. Once we get a java class, kotlin class or specified field the equality of that fields
 * will be same as that we get with shouldBe matcher.
 *
 * @param other the other class to which equality need to be checked.
 * @param ignorePrivateFields specify whether private fields should be ignored in equality check or not, default value is true
 * @param ignoreComputedFields specify whether computed fields should be ignored in equality check or not, default value is true
 * @param useDefaultShouldBeForFields fully qualified names of data type for which we need to use default shouldBe, default empty list.
 *
 * */
@Deprecated(
   message = "This will be removed in Kotest 7.0",
   replaceWith = ReplaceWith("shouldBeEqualToComparingFields(other, fieldEqualityCheckConfig)")
)
fun <T : Any> T.shouldBeEqualToComparingFields(
   other: T,
   ignorePrivateFields: Boolean = true,
   ignoreComputedFields: Boolean = true,
   useDefaultShouldBeForFields: List<String> = emptyList(),
) {
   shouldBeEqualToComparingFields(
      other, FieldsEqualityCheckConfig(
         ignorePrivateFields = ignorePrivateFields,
         ignoreComputedFields = ignoreComputedFields,
         propertiesToExclude = emptyList(),
         useDefaultShouldBeForFields = useDefaultShouldBeForFields
      )
   )
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

/**
 * Matcher that compares values without using field by field comparison.
 *
 * This matcher should be used to check equality of two class for which you want to consider there fields for equality
 * instead of its equals method.
 *
 * This matcher recursively check equality of given values till we get a java or kotlin class. Once we get a java or kotlin
 * class the equality of that fields will be same as that we get with shouldBe matcher.
 *
 * @param other the other class to which equality need to be checked.
 * @param ignorePrivateFields specify whether private fields should be ignored in equality check or not, default value is true
 * @param ignoreComputedFields specify whether computed fields should be ignored in equality check or not, default value is true
 * @param ignoreProperty fields which should be ignored during equality check
 * @param ignoreProperties fields which should be ignored during equality check
 * */
@Deprecated(
   message = "This will be removed in Kotest 7.0",
   replaceWith = ReplaceWith("shouldBeEqualToComparingFields(other, fieldEqualityCheckConfig) or shouldBeEqualToIgnoringFields(other, ignoredField)")
)
fun <T : Any> T.shouldBeEqualToComparingFieldsExcept(
   other: T,
   ignorePrivateFields: Boolean,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>,
   ignoreComputedFields: Boolean = true
) {
   shouldBeEqualToComparingFields(
      other,
      FieldsEqualityCheckConfig(
         ignorePrivateFields = ignorePrivateFields,
         ignoreComputedFields = ignoreComputedFields,
         propertiesToExclude = listOf(ignoreProperty) + ignoreProperties,
      )
   )
}

@Deprecated(
   message = "This will be removed in Kotest 7.0",
   replaceWith = ReplaceWith("shouldNotBeEqualToComparingFields(other, fieldEqualityCheckConfig) or shouldNotBeEqualToIgnoringFields(other, ignoredField)")
)
fun <T : Any> T.shouldNotBeEqualToComparingFieldsExcept(
   other: T,
   ignorePrivateFields: Boolean,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>,
   includeComputedProperties: Boolean = false
) {
   shouldNotBeEqualToComparingFields(
      other,
      FieldsEqualityCheckConfig(
         ignorePrivateFields = ignorePrivateFields,
         ignoreComputedFields = !includeComputedProperties,
         propertiesToExclude = listOf(ignoreProperty) + ignoreProperties
      )
   )
}

@Deprecated(
   message = "This will be removed in Kotest 7.0",
   replaceWith = ReplaceWith("shouldBeEqualToComparingFields(other, fieldEqualityCheckConfig) or shouldBeEqualToIgnoringFields(other, ignoredField)")
)
fun <T : Any> T.shouldBeEqualToComparingFieldsExcept(
   other: T,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>
) {
   shouldBeEqualToComparingFields(
      other,
      FieldsEqualityCheckConfig(propertiesToExclude = listOf(ignoreProperty) + ignoreProperties)
   )
}

@Deprecated(
   message = "This will be removed in Kotest 7.0",
   replaceWith = ReplaceWith("shouldNotBeEqualToComparingFields(other, fieldEqualityCheckConfig) or shouldNotBeEqualToIgnoringFields(other, ignoredField)")
)
fun <T : Any> T.shouldNotBeEqualToComparingFieldsExcept(
   other: T,
   ignoreProperty: KProperty<*>,
   vararg ignoreProperties: KProperty<*>
) {
   shouldNotBeEqualToComparingFields(
      other,
      FieldsEqualityCheckConfig(propertiesToExclude = listOf(ignoreProperty) + ignoreProperties)
   )
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

@Deprecated(
   message = "This will be removed in Kotest 7.0",
   replaceWith = ReplaceWith("shouldNotBeEqualToComparingFields(other, fieldEqualityCheckConfig)")
)
fun <T : Any> T.shouldNotBeEqualToComparingFields(
   other: T,
   ignorePrivateFields: Boolean = true,
   ignoreComputedFields: Boolean = true
) {
   shouldNotBeEqualToComparingFields(
      other, FieldsEqualityCheckConfig(
         ignorePrivateFields = ignorePrivateFields,
         ignoreComputedFields = ignoreComputedFields
      )
   )
}

@Deprecated(
   message = "This will be removed in Kotest 6.0",
   replaceWith = ReplaceWith("beEqualComparingFields(other, fieldEqualityCheckConfig)")
)
fun <T : Any> beEqualComparingFields(
   other: T,
   ignorePrivateFields: Boolean,
   propertiesToExclude: List<KProperty<*>>,
   ignoreComputedFields: Boolean,
   useDefaultShouldBeForFields: List<String> = emptyList(),
) = beEqualComparingFields(
   other, FieldsEqualityCheckConfig(
      ignorePrivateFields = ignorePrivateFields,
      ignoreComputedFields = ignoreComputedFields,
      propertiesToExclude = propertiesToExclude,
      useDefaultShouldBeForFields = useDefaultShouldBeForFields,
   )
)

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

private fun <T> checkEqualityOfFields(fields: List<KProperty<*>>, value: T, other: T): List<String> {
   return fields.mapNotNull {
      val actual = it.getter.call(value)
      val expected = it.getter.call(other)

      val isEqual = eq(actual, expected) == null

      if (isEqual) null else "${it.name}: ${actual.print().value} != ${expected.print().value}"
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
      val typeName = it.returnType.toString().replace("?", "")
      val heading = "${it.name}:"

      if (requiresUseOfDefaultEq(actual, expected, typeName, config.useDefaultShouldBeForFields)) {
         val throwable = eq(actual, expected)
         if (throwable != null) {
            "$heading\n${"\t".repeat(level + 1)}${throwable.message}"
         } else {
            null
         }
      } else {
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
   } to fields
}

private fun requiresUseOfDefaultEq(
   actual: Any?,
   expected: Any?,
   typeName: String,
   useDefaultEqualForFields: List<String>
): Boolean {
   val expectedOrActualIsNull = actual == null || expected == null
   val typeIsJavaOrKotlinBuiltIn by lazy { typeName.startsWith("kotlin.") || typeName.startsWith("java.") }
   val expectedOrActualIsEnum = actual is Enum<*>
      || expected is Enum<*>
      || (actual != null && actual::class.java.isEnum)
      || (expected != null && expected::class.java.isEnum)
   return expectedOrActualIsNull
      || typeIsJavaOrKotlinBuiltIn
      || useDefaultEqualForFields.contains(typeName)
      || expectedOrActualIsEnum
}

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


