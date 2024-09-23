package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Array<T>.shouldContainDuplicates() {
   asList().shouldContainDuplicates()
}

fun <T> Array<T>.shouldNotContainDuplicates(): Array<T> {
   asList().shouldNotContainDuplicates()
   return this
}

fun <T, C : Collection<T>> C.shouldContainDuplicates(): C {
   this should containDuplicates()
   return this
}

fun <T, C : Collection<T>> C.shouldNotContainDuplicates(): C {
   this shouldNot containDuplicates()
   return this
}

fun <T, I : Iterable<T>> I.shouldContainDuplicates(): I {
   toList().shouldContainDuplicates()
   return this
}

fun <T, I : Iterable<T>> I.shouldNotContainDuplicates(): I {
   toList().shouldNotContainDuplicates()
   return this
}

fun <T> containDuplicates(): Matcher<Iterable<T>> = containDuplicates(null)

internal fun <T> containDuplicates(name: String?) = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>): MatcherResult {
      val name = name ?: value.containerName()
      val duplicates = value.duplicates()
      return MatcherResult(
         duplicates.isNotEmpty(),
         { "$name should contain duplicates" },
         { "$name should not contain duplicates, but has some: ${duplicates.print().value}" })
   }
}

internal fun <T> Iterable<T>.duplicates(): List<T> = this.groupingBy { it }
   .eachCount().entries
   .filter { it.value > 1 }
   .map { it.key }
