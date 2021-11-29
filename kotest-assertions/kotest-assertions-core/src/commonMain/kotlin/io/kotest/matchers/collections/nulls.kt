package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T> Iterable<T>.shouldContainOnlyNulls(): Iterable<T> {
   toList().shouldContainOnlyNulls()
   return this
}

fun <T> Array<T>.shouldContainOnlyNulls(): Array<T> {
   asList().shouldContainOnlyNulls()
   return this
}

fun <T> Collection<T>.shouldContainOnlyNulls(): Collection<T> {
   this should containOnlyNulls()
   return this
}

fun <T> Iterable<T>.shouldNotContainOnlyNulls(): Iterable<T> {
   toList().shouldNotContainOnlyNulls()
   return this
}

fun <T> Array<T>.shouldNotContainOnlyNulls(): Array<T> {
   asList().shouldNotContainOnlyNulls()
   return this
}

fun <T> Collection<T>.shouldNotContainOnlyNulls(): Collection<T> {
   this shouldNot containOnlyNulls()
   return this
}

fun <T> containOnlyNulls() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.all { it == null },
         { "Collection should contain only nulls" },
         {
            "Collection should not contain only nulls"
         })
}

fun <T> Iterable<T>.shouldContainNull(): Iterable<T> {
   toList().shouldContainNull()
   return this
}

fun <T> Array<T>.shouldContainNull(): Array<T> {
   asList().shouldContainNull()
   return this
}

fun <T> Collection<T>.shouldContainNull(): Collection<T> {
   this should containNull()
   return this
}

fun <T> Iterable<T>.shouldNotContainNull(): Iterable<T> {
   toList().shouldNotContainNull()
   return this
}

fun <T> Array<T>.shouldNotContainNull(): Array<T> {
   asList().shouldNotContainNull()
   return this
}

fun <T> Collection<T>.shouldNotContainNull(): Collection<T> {
   this shouldNot containNull()
   return this
}

fun <T> containNull() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.any { it == null },
         { "Collection should contain at least one null" },
         { "Collection should not contain any nulls" })
}

fun <T> Iterable<T>.shouldContainNoNulls(): Iterable<T> {
   toList().shouldContainNoNulls()
   return this
}

fun <T> Array<T>.shouldContainNoNulls(): Array<T> {
   asList().shouldContainNoNulls()
   return this
}

fun <T> Collection<T>.shouldContainNoNulls(): Collection<T> {
   this should containNoNulls()
   return this
}

fun <T> Iterable<T>.shouldNotContainNoNulls(): Iterable<T> {
   toList().shouldNotContainNoNulls()
   return this
}

fun <T> Array<T>.shouldNotContainNoNulls(): Array<T> {
   asList().shouldNotContainNoNulls()
   return this
}

fun <T> Collection<T>.shouldNotContainNoNulls(): Collection<T> {
   this shouldNot containNoNulls()
   return this
}

fun <T> containNoNulls() = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.all { it != null },
         { "Collection should not contain nulls" },
         { "Collection should have at least one null" }
      )
}
