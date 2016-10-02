package io.kotlintest.matchers

object contain : ShouldKeyword<contain>

infix fun ShouldBuilder<have, out Collection<*>>.size(expected: Int): Unit {
  val size = value.size
  if (size != expected)
    throw AssertionError("Collection was expected to have size $expected but had size $size")
}

infix fun <T> ShouldBuilder<contain, out Collection<T>>.element(expected: T): Unit {
  if (!value.contains(expected))
    throw AssertionError("Collection did not have expected element $expected")
}

interface CollectionMatchers {

  fun beEmpty(): (Collection<*>) -> Unit {
    return { value ->
      if (value.isNotEmpty())
        throw AssertionError("Collection was expected to be empty but has size ${value.size}")
    }
  }

  fun <T> containInAnyOrder(vararg ts: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>) {
      for (t in ts) {
        if (!value.contains(t))
          throw AssertionError("Collection did not contain value $t")
      }
    }
  }

  fun <T> haveSize(size: Int): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>) {
      if (value.size != size)
        throw AssertionError("Collection did not have size $size")
    }
  }

  fun <T> contain(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>) {
      if (!value.contains(t))
        throw AssertionError("Collection did not contain element $t")
    }
  }

  fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>) {
      if (value.size != 1)
        throw AssertionError("Collection is not a single element but contains ${value.size} elements")
      if (value.first() != t)
        throw AssertionError("Collection contains a single element but it is not equal to $t")
    }
  }

  fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
    override fun test(value: List<T>) {
      if (value.sorted() != value)
        throw AssertionError("Collection $value is not sorted")
    }
  }
}