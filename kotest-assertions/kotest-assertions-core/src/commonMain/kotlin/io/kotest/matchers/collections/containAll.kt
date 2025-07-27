package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.equals.Equality
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.similarity.possibleMatchesForSet

fun <T> Iterable<T>.shouldContainAll(vararg ts: T) = toList().shouldContainAll(*ts)

fun <T> Array<T>.shouldContainAll(vararg ts: T): Array<T> {
   asList().shouldContainAll(*ts)
   return this
}

fun <T> Collection<T>.shouldContainAll(vararg ts: T): Collection<T> {
   this should containAll(*ts)
   return this
}

infix fun <T> Iterable<T>.shouldContainAll(ts: Collection<T>) = toList().shouldContainAll(ts)
infix fun <T> Array<T>.shouldContainAll(ts: Collection<T>): Array<T> {
   asList().shouldContainAll(ts)
   return this
}

infix fun <T> Collection<T>.shouldContainAll(ts: Collection<T>): Collection<T> {
   this should containAll(ts)
   return this
}

fun <T> Iterable<T>.shouldNotContainAll(vararg ts: T): Iterable<T> {
   toList().shouldNotContainAll(*ts)
   return this
}

fun <T> Array<T>.shouldNotContainAll(vararg ts: T): Array<T> {
   asList().shouldNotContainAll(*ts)
   return this
}

fun <T> Collection<T>.shouldNotContainAll(vararg ts: T): Collection<T> {
   this shouldNot containAll(*ts)
   return this
}

infix fun <T> Iterable<T>.shouldNotContainAll(ts: Collection<T>) = toList().shouldNotContainAll(ts)

infix fun <T> Array<T>.shouldNotContainAll(ts: Collection<T>): Array<T> {
   asList().shouldNotContainAll(ts)
   return this
}

infix fun <T> Collection<T>.shouldNotContainAll(ts: Collection<T>): Collection<T> {
   this shouldNot containAll(ts)
   return this
}

fun <T> containAll(vararg ts: T) = containAll(ts.asList())

fun <T> containAll(ts: Collection<T>): Matcher<Collection<T>> =
   containAll(ts, null)

fun <T> containAll(
   ts: Collection<T>,
   verifier: Equality<T>?,
): Matcher<Collection<T>> =
   object : Matcher<Collection<T>> {
      override fun test(value: Collection<T>): MatcherResult {

         val missing = ts.filterNot { t ->
            value.any { verifier?.verify(it, t)?.areEqual() ?: (it == t) }
         }
         val passed = missing.isEmpty()

         val possibleMatchesDescription = {
            possibleMatchesForSet(passed, missing.toSet(), value.toSet(), verifier)
         }

         val failure =
            { "Collection should contain all of ${ts.print().value} but was missing ${missing.print().value}${possibleMatchesDescription()}" }
         val negFailure = { "Collection should not contain all of ${ts.print().value}" }

         return MatcherResult(passed, failure, negFailure)
      }
   }
