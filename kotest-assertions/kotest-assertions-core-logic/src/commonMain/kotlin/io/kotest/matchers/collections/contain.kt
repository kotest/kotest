package io.kotest.matchers.collections

import io.kotest.assertions.equals.Equality
import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesDescription
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

// The dot-notation `shouldContain`/`shouldNotContain` assertions live in kotest-assertions-core-standard
// and the infix forms in kotest-assertions-core-infix. This module keeps the matcher implementation.

// Matcher
fun <T, C : Collection<T>> contain(t: T, verifier: Equality<T> = Equality.default()) = object : Matcher<C> {
   override fun test(value: C) : MatcherResult {
      val passedAtIndexes = value.mapIndexedNotNull {
         index, it -> if(verifier.verify(it, t).areEqual()) index else null
      }
      val passed = passedAtIndexes.isNotEmpty()
      val possibleMatches = {
         if (!passed && (verifier.name() == Equality.default<T>().name())) {
            val candidates = possibleMatchesDescription(value.toSet(), t)
            if (candidates.isEmpty()) "" else "\nPossibleMatches:$candidates"
         } else ""
      }
      return MatcherResult(
         passed,
         {
            "Collection should contain element ${t.print().value} based on ${verifier.name()}; " +
               "but the collection is ${value.print().value}${possibleMatches()}"
         },
         { "Collection should not contain element ${t.print().value} based on ${verifier.name()}, but it did at index(es):${passedAtIndexes.print().value}" }
      )
   }
}
