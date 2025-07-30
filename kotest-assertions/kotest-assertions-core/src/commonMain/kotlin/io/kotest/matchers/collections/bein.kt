package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesForSet
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.jvm.JvmName

/**
 * Verifies that this element is in [collection] by comparing value
 *
 * Assertion to check that this element is in [collection]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [collection] but another instance with same value is present, the
 * test will pass.
 *
 * An empty collection will always fail.
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
infix fun <T> T.shouldBeIn(collection: Collection<T>): T {
   this should beIn(collection)
   return this
}

/**
 * Verifies that this element is NOT any of [collection]
 *
 * Assertion to check that this element is not any of [collection]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [collection], or this will fail.
 *
 * An empty array will always pass (vacuous truth).
 * See: https://en.wikipedia.org/wiki/Vacuous_truth
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
infix fun <T> T.shouldNotBeIn(collection: Collection<T>): T {
   this shouldNot beIn(collection.toList())
   return this
}

/**
 * Verifies that this element is any of [any] by comparing value
 *
 * Assertion to check that this element is any of [any]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not any of [any] but another instance with same value is present, the
 * test will pass.
 *
 * An empty collection will always fail.
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
fun <T> T.shouldBeIn(vararg any: T): T {
   this should beIn(any.toList())
   return this
}

/**
 * Verifies that this element is NOT any of [any]
 *
 * Assertion to check that this element is not any of [any]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [any], or this will fail.
 *
 * An empty array will always pass (vacuous truth).
 * See: https://en.wikipedia.org/wiki/Vacuous_truth
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
fun <T> T.shouldNotBeIn(vararg any: T): T {
   this shouldNot beIn(any.toList())
   return this
}


/**
 * Verifies that this element is in [array] by comparing value
 *
 * Assertion to check that this element is in [array]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [array] but another instance with same value is present, the
 * test will pass.
 *
 * An empty array will always fail.
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
@JvmName("shouldBeInArray")
infix fun <T> T.shouldBeIn(array: Array<T>): T {
   this should beIn(array.toList())
   return this
}

/**
 * Verifies that this element is NOT any of [array]
 *
 * Assertion to check that this element is not any of [array]. This assertion checks by value, and not by reference,
 * therefore any instance with same value must not be in [array], or this will fail.
 *
 * An empty array will always pass (vacuous truth).
 * See: https://en.wikipedia.org/wiki/Vacuous_truth
 *
 * @see [shouldNotBeIn]
 * @see [beIn]
 */
@JvmName("shouldNotBeInArray")
infix fun <T> T.shouldNotBeIn(array: Array<T>): T {
   this shouldNot beIn(array.toList())
   return this
}

/**
 *  Matcher that verifies that this element is in [collection] by comparing value
 *
 * Assertion to check that this element is in [collection]. This assertion checks by value, and not by reference,
 * therefore even if the exact instance is not in [collection] but another instance with same value is present, the
 * test will pass.
 *
 * An empty collection will always fail.
 *
 * @see [shouldBeOneOf]
 * @see [shouldNotBeOneOf]
 */
fun <T> beIn(collection: Collection<T>) = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val match = value in collection

      val possibleMatchesDescription = prefixIfNotEmpty(
         possibleMatchesForSet(match, setOf(value), collection.toSet(), verifier = null),
         "\n"
      )

      return MatcherResult(
         match,
         { "Collection should contain ${value.print().value}, but doesn't. Possible values: ${collection.print().value}$possibleMatchesDescription" },
         {
            "Collection should not contain ${value.print().value}, but does. Forbidden values: ${collection.print().value}"
         })
   }
}

internal fun prefixIfNotEmpty(value: String, prefix: String) = if(value.isEmpty()) "" else "$prefix$value"
