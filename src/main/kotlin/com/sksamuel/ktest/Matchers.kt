package com.sksamuel.ktest

import kotlin.collections.collectionSizeOrDefault
import kotlin.text.startsWith
import kotlin.text.take

interface Matchers {

  infix fun Any.shouldEqual(any: Any): Unit = shouldBe(any)

  infix fun Any.shouldBe(any: Any): Unit {
    if (!this.equals(any)) throw TestFailedException(this.toString() + " did not equal $any")
  }

  fun fail(msg: String) = throw TestFailedException(msg)

  interface HaveWord

  object have : HaveWord

  interface StartWord

  object start : StartWord

  infix fun <T> Iterable<T>.should(have: HaveWord) = IterableMatchers(this)
  infix fun String.should(start: StartWord) = StartStringMatcher(this)
}

class IterableMatchers<T>(val iterable: kotlin.Iterable<T>) {
  infix fun size(k: Int): Unit {
    val size = iterable.collectionSizeOrDefault(0)
    if (size != k) throw TestFailedException("Iterable was expected to have size $k but had size $size")
  }
}

class StartStringMatcher(val string: String) {
  infix fun with(prefix: String): Unit {
    if (!string.startsWith(prefix))
      throw TestFailedException("String does not start wtih $prefix but with ${string.take(10)}")
  }
}
