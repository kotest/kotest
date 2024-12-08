package io.kotest.matchers.sets

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.collections.shouldNotBeOneOf
import io.kotest.matchers.collections.throwEmptyCollectionError
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Set<T>.shouldIntersect(ts: Set<T>) = this should intersectWith(ts)
infix fun <T> Set<T>.shouldNotIntersect(ts: Set<T>) = this shouldNot intersectWith(ts)

/**
 *  Matcher that verifies that this [Set] has at least one common element with another [Set].
 *
 * For instance, the following assertion will pass because two sets have 3 in common:
 *
 * setOf(1, 2, 3) shouldIntersect setOf(3, 4, 5)
 *
 * Likewise, the following assertion will fail because two sets have no common elements:
 *
 * setOf(1, 2, 3) shouldIntersect setOf(4, 5, 6)
 *
 * @see [shouldIntersect]
 * @see [shouldNotIntersect]
 */
fun <T> intersectWith(ts: Set<T>): Matcher<Set<T>> = object : Matcher<Set<T>> {
   override fun test(value: Set<T>): MatcherResult {
      val intersection = ts.intersect(value)
      val passed = intersection.isNotEmpty()

      val failure = { "Set should intersect with ${ts.print().value} but did not." }
      val negFailure = { "Set should not intersect with ${ts.print().value},\nbut had the following common element(s): ${intersection.print().value}" }

      return MatcherResult(passed, failure, negFailure)
   }
}
