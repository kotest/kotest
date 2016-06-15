package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface CollectionMatchers {

  fun beEmpty(): (Collection<*>) -> Unit {
    return { value ->
      if (value.isNotEmpty())
        throw TestFailedException("Collection was expected to be empty but has size ${value.size}")
    }
  }

  infix fun HaveWrapper<out Collection<*>>.size(expected: Int): Unit {
    val size = value.size
    if (size != expected)
      throw TestFailedException("Collection was expected to have size $expected but had size $size")
  }

  infix fun <T> ContainWrapper<out Collection<T>>.element(expected: T): Unit {
    if (!value.contains(expected))
      throw TestFailedException("Collection did not have expected element $expected")
  }
}