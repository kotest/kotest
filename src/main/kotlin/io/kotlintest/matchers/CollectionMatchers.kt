package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface CollectionMatchers {

  infix fun Have<out Collection<*>>.size(expected: Int): Unit {
    val size = value.size
    if (size != expected)
      throw TestFailedException("Collection was expected to have size $expected but had size $size")
  }

  infix fun <T> Contain<out Collection<T>>.element(expected: T): Unit {
    if (!value.contains(expected))
      throw TestFailedException("Collection did not have expected element $expected")
  }
}