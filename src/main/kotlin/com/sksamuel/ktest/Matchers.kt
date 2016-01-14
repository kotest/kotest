package com.sksamuel.ktest

import kotlin.collections.collectionSizeOrDefault

interface Matchers {

  infix fun Any.shouldEqual(any: Any): Unit = shouldBe(any)

  infix fun Any.shouldBe(any: Any): Unit {
    if (!this.equals(any)) throw TestFailedException(this.toString() + " did not equal $any")
  }

  fun fail(msg: String) = throw TestFailedException(msg)

  interface HaveWord

  object have : HaveWord

  infix fun <T> Iterable<T>.should(have: HaveWord) = IterableMatchers(this)
}

class IterableMatchers<T>(val iterable: kotlin.Iterable<T>) {
  infix fun size(k: Int): Unit {
    val size = iterable.collectionSizeOrDefault(0)
    if (size != k) throw TestFailedException("Iterable was expected to have size $k but had size $size")
  }
}
