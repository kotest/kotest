package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.sequences.beLargerThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


infix fun <T, U, I : Iterable<T>> I.shouldBeLargerThan(other: Iterable<U>): I = apply {
   toList() should beLargerThan(other)
}

infix fun <T, U, I : Iterable<T>> I.shouldNotBeLargerThan(other: Iterable<U>): I = apply {
   toList() shouldNot beLargerThan(other)
}

infix fun <T, U> Array<T>.shouldBeLargerThan(other: Array<U>): Array<T> = apply {
   asList().shouldBeLargerThan(other.asList())
}

infix fun <T, U> Array<T>.shouldNotBeLargerThan(other: Array<U>): Array<T> = apply {
   asList().shouldNotBeLargerThan(other.asList())
}

fun <T, U> beLargerThan(other: Iterable<U>) = object : Matcher<Iterable<T>> {
   val otherSize = other.count()
   override fun test(value: Iterable<T>) = MatcherResult(
      value.count() > otherSize,
      { "Collection of size ${value.count()} should be larger than collection of size $otherSize" },
      { "Collection of size ${value.count()} should not be larger than collection of size $otherSize"}
   )
}
