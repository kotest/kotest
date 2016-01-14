package com.sksamuel.ktest

import kotlin.collections.collectionSizeOrDefault
import kotlin.text.*

interface Matchers {

  public infix fun Any.shouldEqual(any: Any): Unit = shouldBe(any)

  public infix fun Any.shouldBe(any: Any): Unit {
    if (!this.equals(any)) throw TestFailedException(this.toString() + " did not equal $any")
  }

  public fun fail(msg: String) = throw TestFailedException(msg)

  interface HaveWord

  public object have : HaveWord

  interface StartWord

  public object start : StartWord

  interface EndWord

  public object end : EndWord

  public infix fun <T> Iterable<T>.should(have: HaveWord) = IterableMatchers(this)
  public infix fun String.should(have: HaveWord) = SubstringMatcher(this)
  public infix fun String.should(start: StartWord) = StartStringMatcher(this)
  public infix fun String.should(end: EndWord) = EndStringMatcher(this)
}

class IterableMatchers<T>(val iterable: kotlin.Iterable<T>) {
  public infix fun size(k: Int): Unit {
    val size = iterable.collectionSizeOrDefault(0)
    if (size != k) throw TestFailedException("Iterable was expected to have size $k but had size $size")
  }
}

class SubstringMatcher(val string: String) {
  public infix fun substring(substr: String): Unit {
    if (!string.contains(substr))
      throw TestFailedException("String does not have substring $substr")
  }
}


class StartStringMatcher(val string: String) {
  public infix fun with(prefix: String): Unit {
    if (!string.startsWith(prefix))
      throw TestFailedException("String does not start with $prefix but with ${string.take(prefix.length)}")
  }
}

class EndStringMatcher(val string: String) {
  public infix fun with(suffix: String): Unit {
    if (!string.endsWith(suffix))
      throw TestFailedException("String does not end with $suffix but with ${string.takeLast(suffix.length)}")
  }
}
