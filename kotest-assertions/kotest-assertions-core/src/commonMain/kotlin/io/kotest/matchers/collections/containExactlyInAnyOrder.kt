package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.equals.Equality
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


/**
 * Verifies that the given collection contains all the specified elements and no others, but in any order.
 *
 * For example, each of the following examples would pass for a collection [1,2,3].
 *
 * collection.shouldContainExactlyInAnyOrder(1, 2, 3)
 * collection.shouldContainExactlyInAnyOrder(3, 2, 1)
 * collection.shouldContainExactlyInAnyOrder(2, 1, 3)
 *
 * Note: Comparison is via the standard Java equals and hash code methods.
 *
 * From the javadocs for hashcode: If two objects are equal according to the equals(Object) method,
 * then calling the hashCode method on each of the two objects must produce the same integer result.
 *
 */
infix fun <T> Array<T>.shouldContainExactlyInAnyOrder(expected: Array<T>): Array<T> {
   asList().shouldContainExactlyInAnyOrder(expected.asList())
   return this
}

/**
 * Verifies that the given collection contains all the specified elements and no others, but in any order.
 *
 * For example, each of the following examples would pass for a collection [1,2,3].
 *
 * collection.shouldContainExactlyInAnyOrder(1, 2, 3)
 * collection.shouldContainExactlyInAnyOrder(3, 2, 1)
 * collection.shouldContainExactlyInAnyOrder(2, 1, 3)
 *
 * Note: Comparison is via the standard Java equals and hash code methods.
 *
 * From the javadocs for hashcode: If two objects are equal according to the equals(Object) method,
 * then calling the hashCode method on each of the two objects must produce the same integer result.
 *
 */
infix fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(expected: Collection<T>?): C? {
   expected.shouldNotBeNull()
   this should containExactlyInAnyOrder(expected)
   return this
}

/**
 * Verifies that the given collection contains all the specified elements and no others, but in any order.
 *
 * For example, each of the following examples would pass for a collection [1,2,3].
 *
 * collection.shouldContainExactlyInAnyOrder(1, 2, 3)
 * collection.shouldContainExactlyInAnyOrder(3, 2, 1)
 * collection.shouldContainExactlyInAnyOrder(2, 1, 3)
 *
 * Note: Comparison is via the standard Java equals and hash code methods.
 *
 * From the javadocs for hashcode: If two objects are equal according to the equals(Object) method,
 * then calling the hashCode method on each of the two objects must produce the same integer result.
 *
 */
fun <T, C : Collection<T>> C?.shouldContainExactlyInAnyOrder(vararg expected: T): C? {
   this should containExactlyInAnyOrder(*expected)
   return this
}

/**
 * Verifies that the given collection contains all the specified elements and no others, but in any order.
 *
 * For example, each of the following examples would pass for a collection [1,2,3].
 *
 * collection.shouldContainExactlyInAnyOrder(1, 2, 3)
 * collection.shouldContainExactlyInAnyOrder(3, 2, 1)
 * collection.shouldContainExactlyInAnyOrder(2, 1, 3)
 *
 * Note: Comparison is via the standard Java equals and hash code methods.
 *
 * From the javadocs for hashcode: If two objects are equal according to the equals(Object) method,
 * then calling the hashCode method on each of the two objects must produce the same integer result.
 *
 */
fun <T> containExactlyInAnyOrder(vararg expected: T): Matcher<Collection<T>?> =
   containExactlyInAnyOrder(expected.asList())

infix fun <T> Array<T>.shouldNotContainExactlyInAnyOrder(expected: Array<T>): Array<T> {
   asList().shouldNotContainExactlyInAnyOrder(expected.asList())
   return this
}

infix fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(expected: Collection<T>?): C? {
   expected.shouldNotBeNull()
   this shouldNot containExactlyInAnyOrder(expected)
   return this
}

fun <T, C : Collection<T>> C?.shouldNotContainExactlyInAnyOrder(vararg expected: T): C? {
   this shouldNot containExactlyInAnyOrder(*expected)
   return this
}

/** Assert that a collection contains exactly the given values and nothing else, in any order. */
fun <T, C : Collection<T>> containExactlyInAnyOrder(expected: C) =
   containExactlyInAnyOrder(expected, null)

/** Assert that a collection contains exactly the given values and nothing else, in any order. */
fun <T, C : Collection<T>> containExactlyInAnyOrder(
   expected: C,
   verifier: Equality<T>?,
): Matcher<C?> = neverNullMatcher { actual ->

   val valueGroupedCounts: Map<T, Int> = actual.groupBy { it }.mapValues { it.value.size }
   val expectedGroupedCounts: Map<T, Int> = expected.groupBy { it }.mapValues { it.value.size }

   val passed = expectedGroupedCounts.size == valueGroupedCounts.size
      && expectedGroupedCounts.all { (k, v) ->
      valueGroupedCounts.filterKeys { verifier?.verify(k, it)?.areEqual() ?: (k == it) }[k] == v
   }

   val missing = expected.filterNot { t ->
      actual.any { verifier?.verify(it, t)?.areEqual() ?: (t == it) }
   }
   val extra = actual.filterNot { t ->
      expected.any { verifier?.verify(it, t)?.areEqual() ?: (t == it) }
   }
   val countMismatch = countMismatch(expectedGroupedCounts, valueGroupedCounts)

   val failureMessage = {
      buildString {
         append("Collection should contain ${expected.print().value} in any order, but was ${actual.print().value}")
         appendLine()
         appendMissingAndExtra(missing, extra)
         if(missing.isNotEmpty() || extra.isNotEmpty()) {
            appendLine()
         }
         if(countMismatch.isNotEmpty()) {
            append("CountMismatches: ${countMismatch.joinToString(", ")}")
         }
      }
   }

   val negatedFailureMessage = { "Collection should not contain exactly ${expected.print().value} in any order" }

   MatcherResult(
      passed,
      failureMessage,
      negatedFailureMessage
   )
}

internal fun<T> countMismatch(expectedCounts: Map<T, Int>, actualCounts: Map<T, Int>) =
   actualCounts.entries.mapNotNull { actualEntry ->
      expectedCounts[actualEntry.key]?.let { expectedValue ->
         if(actualEntry.value != expectedValue)
            CountMismatch(actualEntry.key, expectedValue, actualEntry.value)
         else null
      }
   }

internal data class CountMismatch<T>(val key: T, val expectedCount: Int, val actualCount: Int) {
   init {
       require(expectedCount >= 0 && actualCount >= 0) {
          "Both expected and actual count should be non-negative, but expected was: $expectedCount and actual: was: $actualCount"
       }
   }

   override fun toString(): String = "Key=\"${key}\", expected count: $expectedCount, but was: $actualCount"
}
