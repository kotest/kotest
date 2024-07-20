package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.equals.Equality
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.similarity.possibleMatchesDescription

// Infix
infix fun <T> Iterable<T>.shouldNotContain(t: T): Iterable<T> = shouldNotContain(t, Equality.default())
infix fun <T> Array<T>.shouldNotContain(t: T): Array<T> = shouldNotContain(t, Equality.default())
infix fun <T> Iterable<T>.shouldContain(t: T): Iterable<T> = shouldContain(t, Equality.default())
infix fun <T> Array<T>.shouldContain(t: T): Array<T> = shouldContain(t, Equality.default())

// Should not
fun <T> Iterable<T>.shouldNotContain(t: T, comparator: Equality<T>): Iterable<T> = apply {
   toList() shouldNot contain(t, comparator)
}

fun <T> Array<T>.shouldNotContain(t: T, comparator: Equality<T>): Array<T> = apply {
   asList().shouldNotContain(t, comparator)
}

// Should
fun <T> Iterable<T>.shouldContain(t: T, comparator: Equality<T>): Iterable<T> = apply {
   toList() should contain(t, comparator)
}

fun <T> Array<T>.shouldContain(t: T, comparator: Equality<T>): Array<T> = apply {
   asList().shouldContain(t, comparator)
}

// Matcher
fun <T, C : Collection<T>> contain(t: T, verifier: Equality<T> = Equality.default()) = object : Matcher<C> {
   override fun test(value: C) : MatcherResult {
      val passed = value.any { verifier.verify(it, t).areEqual() }
      val possibleMatches = if(!passed && (verifier.name() == Equality.default<T>().name())) {
         val candidates = possibleMatchesDescription(value.toSet(), t)
         if(candidates.isEmpty()) "" else "\nPossibleMatches:$candidates"
      } else ""
      return MatcherResult(
         passed,
         {
            "Collection should contain element ${t.print().value} based on ${verifier.name()}; " +
               "but the collection is ${value.print().value}$possibleMatches"
         },
         { "Collection should not contain element ${t.print().value} based on ${verifier.name()}" }
      )
   }
}
