package io.kotlintest.matchers

import io.kotlintest.Matcher

@Deprecated("This method was moved. Use the alternative instead.", ReplaceWith("haveSizeMatcher(size)", "io.kotlintest.matchers.collections"))
fun <T> haveSizeMatcher(size: Int) = io.kotlintest.matchers.collections.haveSizeMatcher<T>(size)

@Deprecated("This method was moved. Use the alternative instead.", ReplaceWith("beEmpty()", "io.kotlintest.matchers.collections"))
fun <T> beEmpty(): Matcher<Collection<T>> = io.kotlintest.matchers.collections.beEmpty()

@Deprecated("Method was moved. Use the replacement instead.", ReplaceWith("containAll(ts)", "io.kotlintest.matchers.collections"))
fun <T> containAll(vararg ts: T) = io.kotlintest.matchers.collections.containAll(ts.asList())

@Deprecated("Method was moved. Use the replacement instead", ReplaceWith("containAll(ts)", "io.kotlintest.matchers.collections"))
fun <T> containAll(ts: Collection<T>): Matcher<Collection<T>> = io.kotlintest.matchers.collections.containAll(ts)

@Deprecated("Method was moved. Use the repleacement instead.", ReplaceWith("containsInOrder(ts)", "io.kotlintest.matchers.collections"))
fun <T> containsInOrder(vararg ts: T): Matcher<Collection<T>?> = io.kotlintest.matchers.collections.containsInOrder(ts.asList())

@Deprecated("Method was moved. Use the replacement instead.", ReplaceWith("containsInOrder(subsequence)", "io.kotlintest.matchers.collections"))
fun <T> containsInOrder(subsequence: List<T>): Matcher<Collection<T>?> = io.kotlintest.matchers.collections.containsInOrder(subsequence)

@Deprecated("This method was moved. Use the alternative instead.", ReplaceWith("haveSize(size)", "io.kotlintest.matchers.collections"))
fun <T> haveSize(size: Int): Matcher<Collection<T>> = io.kotlintest.matchers.collections.haveSize(size)

@Deprecated("This method was moved. Use the alternative instead.", ReplaceWith("singleElement(t)", "io.kotlintest.matchers.collections"))
fun <T> singleElement(t: T): Matcher<Collection<T>> = io.kotlintest.matchers.collections.singleElement(t)

@Deprecated("This method was moved. Use the alternative instead.", ReplaceWith("beSorted()", "io.kotlintest.matchers.collections"))
fun <T : Comparable<T>> beSorted(): Matcher<List<T>> = io.kotlintest.matchers.collections.sorted()
@Deprecated("This method was moved. Use the alternative instead.", ReplaceWith("sorted()", "io.kotlintest.matchers.collections"))
fun <T : Comparable<T>> sorted(): Matcher<List<T>> = io.kotlintest.matchers.collections.sorted()