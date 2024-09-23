package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Array<T>.shouldContainDuplicates(): Array<T> {
   asList() should containDuplicates("Array")
   return this
}

fun <T> Array<T>.shouldNotContainDuplicates(): Array<T> {
   asList() shouldNot containDuplicates("Array")
   return this
}

fun <T, C : Collection<T>> C.shouldContainDuplicates(): C {
   this should containDuplicates(null)
   return this
}

fun <T, C : Collection<T>> C.shouldNotContainDuplicates(): C {
   this shouldNot containDuplicates(null)
   return this
}

fun <T, I : Iterable<T>> I.shouldContainDuplicates(): I {
   this should containDuplicates(null)
   return this
}

fun <T, I : Iterable<T>> I.shouldNotContainDuplicates(): I {
   this shouldNot containDuplicates(null)
   return this
}

fun <T> containDuplicates(): Matcher<Iterable<T>> = containDuplicates(null)

private val setResult: MatcherResult by lazy {
   // "Set should contain duplicates" is obviously nonsense, but kept for message consistency
   MatcherResult(false, { "Set should contain duplicates" }, { "Set should not contain duplicates" })
}

internal fun <T> containDuplicates(name: String?) = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>): MatcherResult {
      if (value is Set<*>) {
         return setResult
      }

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
