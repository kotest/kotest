package io.kotlintest.matchers

fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(value.size == size, "Collection should have size $size but has size ${value.size}")
}

fun <T> containsMatcher(t: T) = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(value.contains(t), "Collection should contain element $t")
}

fun <T> beEmpty(): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>): Result = Result(value.isEmpty(), "Collection should be empty")
}

fun <T> containsAll(vararg ts: T) = containsAll(ts.asList())
fun <T> containsAll(ts: List<T>): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) =
      Result(ts.all { value.contains(it) }, "Collection should contain values $ts")
}

// should contain the expected list in order, but allows duplicates,
// so listOf(1, 2, 2, 3, 3, 3, 4, 4) should containInOrder(listOf(1,4)) is true
fun <T : Comparable<T>> containsInOrder(vararg ts: T) = containsInOrder(ts.asList())

fun <T : Comparable<T>> containsInOrder(expected: List<T>) = object : Matcher<List<T>> {
  override fun test(value: List<T>): Result {
    assert(expected.isNotEmpty(), { "expected values must not be empty" })
    assert(expected.sorted() == expected, { "expected values must be sorted but was $expected" })

    var cursor = 0
    var passed = true
    // to pass, all the indexes of element n must occur before the indexes of element n+1,n+2,...
    expected.forEach { expected ->
      val indexes = value.withIndex().filter { it.value == expected }.map { it.index }
      if (indexes.isEmpty()) {
        passed = false
      }
      indexes.forEach {
        if (passed && it < cursor) passed = false
        else cursor = indexes.max()!!
      }
    }

    val errorMessage = "[$value] did not contain the same elements in order as [$expected]"
    return Result(passed, errorMessage)
  }
}

fun <T> haveSize(size: Int): Matcher<Collection<T>> = haveSizeMatcher(size)

fun <T> contain(t: T): Matcher<Collection<T>> = containsMatcher(t)

fun <T> singleElement(t: T): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
  override fun test(value: Collection<T>) = Result(value.size == 1 && value.first() == t, "Collection should be a single element of $t but is $value")
}

fun <T : Comparable<T>> sorted(): Matcher<List<T>> = object : Matcher<List<T>> {
  override fun test(value: List<T>) = Result(value.sorted() == value, "Collection $value should be sorted")
}