package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <T, I : Iterable<T>> I.shouldContainOnlyNulls(): I {
   toList().shouldContainOnlyNulls()
   return this
}

fun <T> Array<T>.shouldContainOnlyNulls(): Array<T> {
   asList().shouldContainOnlyNulls()
   return this
}

fun <T, C : Collection<T>> C.shouldContainOnlyNulls(): C {
   this should containOnlyNulls()
   return this
}

fun <T, I : Iterable<T>> I.shouldNotContainOnlyNulls(): I {
   toList().shouldNotContainOnlyNulls()
   return this
}

fun <T> Array<T>.shouldNotContainOnlyNulls(): Array<T> {
   asList().shouldNotContainOnlyNulls()
   return this
}

fun <T, C : Collection<T>> C.shouldNotContainOnlyNulls(): C {
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

fun <T, I : Iterable<T>> I.shouldContainNull(): I {
   toList().shouldContainNull()
   return this
}

fun <T> Array<T>.shouldContainNull(): Array<T> {
   asList().shouldContainNull()
   return this
}

fun <T, C : Collection<T>> C.shouldContainNull(): C {
   this should containNull()
   return this
}

fun <T, I : Iterable<T>> I.shouldNotContainNull(): I {
   toList().shouldNotContainNull()
   return this
}

fun <T> Array<T>.shouldNotContainNull(): Array<T> {
   asList().shouldNotContainNull()
   return this
}

fun <T, C : Collection<T>> C.shouldNotContainNull(): C {
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

fun <T, I : Iterable<T>> I.shouldContainNoNulls(): I {
   toList().shouldContainNoNulls()
   return this
}

fun <T> Array<T>.shouldContainNoNulls(): Array<T> {
   asList().shouldContainNoNulls()
   return this
}

fun <T, C : Collection<T>> C.shouldContainNoNulls(): C {
   this should containNoNulls()
   return this
}

fun <T, I : Iterable<T>> I.shouldNotContainNoNulls(): I {
   toList().shouldNotContainNoNulls()
   return this
}

fun <T> Array<T>.shouldNotContainNoNulls(): Array<T> {
   asList().shouldNotContainNoNulls()
   return this
}

fun <T, C : Collection<T>> C.shouldNotContainNoNulls(): C {
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
