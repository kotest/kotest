package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.*
import kotlin.jvm.JvmName
import io.kotest.assertions.AssertionsConfig.maxCollectionPrintSize as printSize

/**
 * Assert that a collection contains only the given elements.
 */
@JvmName("shouldContainOnly_iterable")
infix fun <T> Iterable<T>?.shouldContainOnly(expected: Iterable<T>) =
    this?.toList() should containOnly(expected.toList())


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
