package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface CollectionMatchers {

  infix fun Have<out Collection<*>>.size(expected: Int): Unit {
    val size = value.size
    if (size != expected)
      throw TestFailedException("Collection was expected to have size $expected but had size $size")
  }
}