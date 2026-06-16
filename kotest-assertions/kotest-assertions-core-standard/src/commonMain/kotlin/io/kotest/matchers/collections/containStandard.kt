package io.kotest.matchers.collections

import io.kotest.assertions.equals.Equality
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// Should not
fun <T, I : Iterable<T>> I.shouldNotContain(t: T, comparator: Equality<T>): I = apply {
   toList() shouldNot contain(t, comparator)
}

fun <T> Array<T>.shouldNotContain(t: T, comparator: Equality<T>): Array<T> = apply {
   asList().shouldNotContain(t, comparator)
}

// Should
fun <T, I : Iterable<T>> I.shouldContain(t: T, comparator: Equality<T>): I = apply {
   toList() should contain(t, comparator)
}

fun <T> Array<T>.shouldContain(t: T, comparator: Equality<T>): Array<T> = apply {
   asList().shouldContain(t, comparator)
}
