package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.equals.CommutativeEquality
import io.kotest.equals.Equality
import io.kotest.equals.countByEquality
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.similarity.possibleMatchesDescription


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

   val valueGroupedCounts: Map<T, Int> = getGroupedCount(actual, verifier)
   val expectedGroupedCounts: Map<T, Int> = getGroupedCount(expected, verifier)

   val missing = expected.filterNot { t ->
      actual.any { verifier?.verify(it, t)?.areEqual() ?: (t == it) }
   }
   val extra = actual.filterNot { t ->
      expected.any { verifier?.verify(it, t)?.areEqual() ?: (t == it) }
   }
<<<<<<< HEAD
   val countMismatch = countMismatch(expectedGroupedCounts, valueGroupedCounts, verifier)
   val passed = missing.isEmpty() && extra.isEmpty() && countMismatch.isEmpty()
=======
   val countMismatch = countMismatch(expectedGroupedCounts, valueGroupedCounts)
>>>>>>> master
   val possibleMatches = extra
      .map { possibleMatchesDescription(expected.toSet(), it) }
      .filter { it.isNotEmpty() }
      .joinToString("\n")

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
         if(possibleMatches.isNotEmpty()) {
            appendLine()
            append("Possible matches for unexpected elements:\n$possibleMatches")
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

private fun <C : Collection<T>, T> getGroupedCount(actual: C, verifier: Equality<T>?) =
   if(verifier == null) {
      actual.groupBy { it }.mapValues { it.value.size }
   } else {
      actual.countByEquality(verifier)
   }

internal fun<T> countMismatch(
   expectedCounts: Map<T, Int>,
   actualCounts: Map<T, Int>,
   verifier: Equality<T>?
): List<CountMismatch<T>> {
   if(verifier == null) {
      return actualCounts.entries.mapNotNull { actualEntry ->
         expectedCounts[actualEntry.key]?.let { expectedValue ->
            if (actualEntry.value != expectedValue)
               CountMismatch(actualEntry.key, expectedValue, actualEntry.value)
            else null
         }
      }
   }
   val commutativeVerifier = CommutativeEquality(verifier)
   return actualCounts.entries.mapNotNull { actualEntry ->
      val equalKeyInExpected =
         expectedCounts.keys.firstOrNull { expectedKey ->
            commutativeVerifier.verify(expectedKey, actualEntry.key).areEqual()
         } ?: actualEntry.key
      expectedCounts[equalKeyInExpected]?.let { expectedValue ->
         if (actualEntry.value != expectedValue)
            CountMismatch(actualEntry.key, expectedValue, actualEntry.value)
         else null
      }
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
