package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.*
import kotlin.jvm.JvmName
import io.kotest.assertions.AssertionsConfig.maxCollectionPrintSize as printSize

/**
 * Assert that a collection contains only the given elements.
 * For example,
 *  listOf(1, 1, 2) shouldContainOnly listOf(1, 2)    // Assertion passes
 *  listOf(1, 2, 1) shouldContainOnly listOf(1)       // Assertion fails
 *  listOf(1) shouldContainOnly listOf(2, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldContainOnly_iterable")
infix fun <T> Iterable<T>?.shouldContainOnly(expected: Iterable<T>) =
    this?.toList() should containOnly(expected.toList())

/**
 * Assert that an array contains only the given elements.
 * For example,
 *  arrayOf(1, 1, 2) shouldContainOnly arrayOf(1, 2)    // Assertion passes
 *  arrayOf(1, 2, 1) shouldContainOnly arrayOf(1)       // Assertion fails
 *  arrayOf(1) shouldContainOnly arrayOf(2, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldContainOnly_array")
infix fun <T> Array<T>?.shouldContainOnly(expected: Array<T>) =
    this?.asList() should containOnly(*expected)

/**
 * Assert that a collection contains only the given elements.
 * For example,
 *  listOf(1, 1, 2) shouldContainOnly(1, 2)    // Assertion passes
 *  listOf(1, 2, 1) shouldContainOnly(1)       // Assertion fails
 *  listOf(1) shouldContainOnly(2, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
fun <T> Iterable<T>?.shouldContainOnly(vararg expected: T) =
    this?.toList() should containOnly(*expected)

/**
 * Assert that an array contains only the given elements.
 * For example,
 *  arrayOf(1, 1, 2) shouldContainOnly(1, 2)    // Assertion passes
 *  arrayOf(1, 2, 1) shouldContainOnly(1)       // Assertion fails
 *  arrayOf(1) shouldContainOnly(2, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
fun <T> Array<T>?.shouldContainOnly(vararg expected: T) =
    this?.asList() should containOnly(*expected)

/**
 * Assert that a collection contains only the given elements.
 * For example,
 *  listOf(1, 1, 2) shouldContainOnly listOf(1, 2)    // Assertion passes
 *  listOf(1, 2, 1) shouldContainOnly listOf(1)       // Assertion fails
 *  listOf(1) shouldContainOnly listOf(2, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
infix fun <T, C : Collection<T>> C?.shouldContainOnly(expected: C) = this should containOnly(expected)

/**
 * Assert that a collection contains only the given elements.
 * For example,
 *  listOf(1, 1, 2) shouldContainOnly(1, 2)    // Assertion passes
 *  listOf(1, 2, 1) shouldContainOnly(1)       // Assertion fails
 *  listOf(1) shouldContainOnly(2, 2)          // Assertion fails
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
fun <T> Collection<T>?.shouldContainOnly(vararg expected: T) = this should containOnly(*expected)

/**
 * Assert that a collection contains only the given elements
 */
fun <T> containOnly(vararg expected: T): Matcher<Collection<T>?> = containOnly(expected.asList())

/**
 * Assert that a collection contains only the given elements.
 */
fun <T, C : Collection<T>> containOnly(expectedCollection: C): Matcher<C?> = neverNullMatcher { actualCollection ->
    val actualSet = actualCollection.toSet()
    val expectedSet = expectedCollection.toSet()
    val notExpectedSet = actualSet.minus(expectedSet)
    val missingSet = expectedSet.minus(actualSet)

    val passed = notExpectedSet.isEmpty() && missingSet.isEmpty()
    val negatedFailureMessageSupplier = { "Collection should not have just ${expectedCollection.print().value}" }
    val failureMessageSupplier = {
        buildString {
            if (missingSet.isNotEmpty()) {
                append("Some elements were missing: ${missingSet.take(printSize.value).print().value}")
            }
            if (missingSet.isNotEmpty() && notExpectedSet.isNotEmpty()) {
                append(" and some elements were unexpected: ${notExpectedSet.take(printSize.value).print().value}")
            }
            if (missingSet.isEmpty() && notExpectedSet.isNotEmpty()) {
                append("Some elements were unexpected: ${notExpectedSet.take(printSize.value).print().value}")
            }
            appendLine()
        }
    }

    MatcherResult(passed, failureMessageSupplier, negatedFailureMessageSupplier)
}

/**
 * Assert that a collection should not have only the given elements
 * For example,
 *  listOf(1, 1, 2) shouldNotContainOnly listOf(1, 2)    // Assertion fails
 *  listOf(1, 2, 1) shouldNotContainOnly listOf(1)       // Assertion passes
 *  listOf(1) shouldNotContainOnly listOf(2, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldNotContainOnly_iterable")
infix fun <T> Iterable<T>?.shouldNotContainOnly(expected: Iterable<T>) =
    this?.toList() shouldNot containOnly(expected.toList())

/**
 * Assert that an array should not have only the given elements
 * For example,
 *  arrayOf(1, 1, 2) shouldNotContainOnly arrayOf(1, 2)    // Assertion fails
 *  arrayOf(1, 2, 1) shouldNotContainOnly arrayOf(1)       // Assertion passes
 *  arrayOf(1) shouldNotContainOnly arrayOf(2, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
@JvmName("shouldNotContainOnly_array")
infix fun <T> Array<T>?.shouldNotContainOnly(expected: Array<T>) = this?.asList() shouldNot containOnly(*expected)

/**
 * Assert that a collection should not have only the given elements
 * For example,
 *  listOf(1, 1, 2) shouldNotContainOnly(1, 2)    // Assertion fails
 *  listOf(1, 2, 1) shouldNotContainOnly(1)       // Assertion passes
 *  listOf(1) shouldNotContainOnly(2, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
fun <T> Iterable<T>?.shouldNotContainOnly(vararg expected: T) = this?.toList() shouldNot containOnly(*expected)

/**
 * Assert that an array should not have only the given elements
 * For example,
 *  arrayOf(1, 1, 2) shouldNotContainOnly(1, 2)    // Assertion fails
 *  arrayOf(1, 2, 1) shouldNotContainOnly(1)       // Assertion passes
 *  arrayOf(1) shouldNotContainOnly(2, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
fun <T> Array<T>?.shouldNotContainOnly(vararg expected: T) = this?.asList() shouldNot containOnly(*expected)

/**
 * Assert that a collection should not have only the given elements
 * For example,
 *  listOf(1, 1, 2) shouldNotContainOnly listOf(1, 2)    // Assertion fails
 *  listOf(1, 2, 1) shouldNotContainOnly listOf(1)       // Assertion passes
 *  listOf(1) shouldNotContainOnly listOf(2, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
infix fun <T, C : Collection<T>> C?.shouldNotContainOnly(expected: C) = this shouldNot containOnly(expected)

/**
 * Assert that a collection should not have only the given elements
 * For example,
 *  listOf(1, 1, 2) shouldNotContainOnly(1, 2)    // Assertion fails
 *  listOf(1, 2, 1) shouldNotContainOnly(1)       // Assertion passes
 *  listOf(1) shouldNotContainOnly(2, 2)          // Assertion passes
 *
 *  Note: Comparison is via the standard Java equals and hash code methods.
 */
fun <T> Collection<T>?.shouldNotContainOnly(vararg expected: T) = this shouldNot containOnly(*expected)
