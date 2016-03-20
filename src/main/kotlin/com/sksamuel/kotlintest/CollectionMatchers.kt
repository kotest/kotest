package com.sksamuel.kotlintest

class CollectionMatchers<T>(val iterable: Collection<T>) {
  public infix fun size(k: Int): Unit {
    val size = iterable.size
    if (size != k) throw TestFailedException("Iterable was expected to have size $k but had size $size")
  }
}