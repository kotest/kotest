package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Iterable<T>.shouldContainDuplicates(): Iterable<T> {
   toList().shouldContainDuplicates()
   return this
}

fun <T> Array<T>.shouldContainDuplicates() {
   asList().shouldContainDuplicates()
}

fun <T> Collection<T>.shouldContainDuplicates(): Collection<T> {
   this should containDuplicates()
   return this
}

fun <T> Iterable<T>.shouldNotContainDuplicates(): Iterable<T> {
   toList().shouldNotContainDuplicates()
   return this
}

fun <T> Array<T>.shouldNotContainDuplicates(): Array<T> {
   asList().shouldNotContainDuplicates()
   return this
}

fun <T> Collection<T>.shouldNotContainDuplicates(): Collection<T> {
   this shouldNot containDuplicates()
   return this
}

fun <T> containDuplicates() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val duplicates = value.duplicates()
      return MatcherResult(
         duplicates.isNotEmpty(),
         { "Collection should contain duplicates" },
         {
            "Collection should not contain duplicates, but has some: ${duplicates.print().value}"
         })
   }
}

internal fun<T> Collection<T>.duplicates(): Collection<T> = this.groupingBy { it }
   .eachCount().entries
   .mapNotNull { when(it.value) {
      1 -> null
      else -> it.key
   } }
