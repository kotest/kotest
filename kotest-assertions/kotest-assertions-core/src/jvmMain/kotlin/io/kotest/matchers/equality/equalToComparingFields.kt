package io.kotest.matchers.equality

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

infix fun <T : Any> T.shouldBeEqualUsingFields(other: T): T {
   val config = FieldEqualityConfig()
   this should beEqualUsingFields(other, config)
   return this
}

infix fun <T : Any> T.shouldNotBeEqualUsingFields(other: T): T {
   val config = FieldEqualityConfig()
   this shouldNot beEqualUsingFields(other, config)
   return this
}

/**
 * Matcher that compares values using field by field comparison.
 *
 * This matcher should be used to check equality of two class for which you want to consider their fields for equality
 * instead of its `equals` method.
 *
 * This matcher recursively check equality of given values till we get a java class, kotlin class or fields for which we have
 * specified to use default shouldBe. Once we get a java class, kotlin class or specified field the equality of that fields
 * will be same as that we get with shouldBe matcher.
 *
 * @param block a configure block that can be use to configure the match, this must return the value to compare
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
 *
 *  class AnotherNestedClass(val buffer: Buffer) {
 *    val aComputedField: Int
 *      get() = Random.nextInt()
 *  }
 *
 *  class SomeClass(val name: String, val randomId: UUID ,val nestedField: ANestedClass)
 *
 *  someClass shouldBeEqualUsingFields {
 *
 *    ignorePrivateFields = true
 *    ignoreComputedFields = true
 *    propertiesToExclude = listOf(SomeClass::randomId)
 *    useDefaultShouldBeForFields = listOf("org.foo.bar.domain.AnotherNestedClass")
 *
 *    anotherInstanceOfSomeClass
 *  }
 *  ```
 * */
infix fun <T : Any> T.shouldBeEqualUsingFields(block: FieldEqualityConfig.() -> T): T {
   val config = FieldEqualityConfig()
   val other = block.invoke(config)
   this should beEqualUsingFields(other, config)
   return this
}

infix fun <T : Any> T.shouldNotBeEqualUsingFields(block: FieldEqualityConfig.() -> T): T {
   val config = FieldEqualityConfig()
   val other = block.invoke(config)
   this shouldNot beEqualUsingFields(other, config)
   return this
}

/**
 * Config for controlling the way shouldBeEqualUsingFields compares fields.
 *
 * Note: If both [includedProperties] and [excludedProperties] are not empty, an error will be thrown.
 *
 *
 * @property ignorePrivateFields specify whether to exclude private fields in comparison. Default true.
 * @property ignoreComputedFields specify whether to exclude computed fields in comparison. Default true.
 * @property includedProperties specify which fields to include. Default empty list.
 * @property excludedProperties specify which fields to exclude in comparison. Default emptyList.
 * @property useDefaultShouldBeForFields data types for which to use the standard shouldBe comparision
 *                                       instead of recursive field by field comparison.
 * */
class FieldEqualityConfig {
   var ignorePrivateFields: Boolean = true
   var ignoreComputedFields: Boolean = true
   var includedProperties: Collection<KProperty<*>> = emptySet()
   var excludedProperties: Collection<KProperty<*>> = emptySet()
   var useDefaultShouldBeForFields: Collection<KClass<*>> = emptySet()
}

fun <T : Any> beEqualUsingFields(expected: T, config: FieldEqualityConfig): Matcher<T> {

   if (config.includedProperties.isNotEmpty() && config.excludedProperties.isNotEmpty())
      error("Cannot set both includedProperties and excludedProperties")

   return object : Matcher<T> {
      override fun test(value: T): MatcherResult {
         return runCatching { compareUsingFields(value, expected, config) }.fold(
            { result ->
               MatcherResult(
                  result.errors.isEmpty(),
                  {
                     """Expected ${value.print().value} to equal ${expected.print().value}
                  |
                  |Using fields:
                  |${result.fields.joinToString("\n") { " - $it" }}
                  |
                  |Fields that differ:
                  |${result.errors.entries.flatMap { printDifference(it.key, it.value.message ?: "") }.joinToString("\n")}"
                  |
               """.trimMargin()
                  },
                  {
                     """Expected ${value.print().value} to not equal ${expected.print().value}
                  |
                  |Using fields:
                  |${result.fields.joinToString("\n") { " - $it" }}
                  |
               """.trimMargin()
                  }
               )
            },
            {
               MatcherResult(
                  false,
                  {
                     """Error using shouldBeEqualUsingFields matcher
                     |${it.message}
                  """.trimMargin()
                  },
                  {
                     """Error using shouldBeEqualUsingFields matcher
                     |${it.message}
                  """.trimMargin()
                  })
            }
         )
      }

      private fun printDifference(key: String, message: String): List<String> {
         val prefix = " - $key  =>  "
         return message.split("\n").mapIndexed { index, line ->
            if (index == 0) "$prefix$line" else "${" ".repeat(prefix.length)}$line"
         }
      }
   }
}
