package io.kotlintest.matchers

object contain : Keyword

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(value.size == size, "Collection should have size $size")
}

fun <T> containsMatcher(t: T) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(value.contains(t), "Collection should contain element $t")
}

interface CollectionMatchers {

  fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>): Result = Result(value.isEmpty(), "Collection should be empty")
  }

  fun <T> containsAll(vararg ts: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>) =
        Result(ts.all { value.contains(it) }, "Collection should contain values $ts")
  }

  fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

  fun <T> contain(t: T): Matcher<Collection<T>> = containsMatcher(t)

  fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>) = Result(value.size == 1 && value.first() == t, "Collection should be a single element of $t but is $value")
  }

  fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
    override fun test(value: List<T>) = Result(value.sorted() == value, "Collection $value should be sorted")
  }
}