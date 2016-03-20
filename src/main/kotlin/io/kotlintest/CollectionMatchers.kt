package io.kotlintest

interface CollectionMatchers {

  infix fun Have<out Collection<*>>.size(expected: Int): (Collection<*>) -> Unit {
    return { collection ->
      val size = collection.size
      if (size != expected)
        throw TestFailedException("Collection was expected to have size $expected but had size $size")
    }
  }
}