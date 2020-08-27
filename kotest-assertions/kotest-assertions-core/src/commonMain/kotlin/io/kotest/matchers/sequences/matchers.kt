package io.kotest.matchers.sequences

import io.kotest.matchers.*

private fun <T> Sequence<T>.toString(limit: Int = 10) = this.joinToString(", ", limit = limit)

/*
How should infinite sequences be detected, and how should they be dealt with?

Sequence<T>.count() may run through the whole sequence (sequences from `generateSequence()` do so), so isn't always a safe way to detect infinite sequences.

For now, the documentation should mention that infinite sequences will cause these matchers never to halt.
*/

fun <T> Sequence<T>.shouldContainOnlyNulls() = this should containOnlyNulls()
fun <T> Sequence<T>.shouldNotContainOnlyNulls() = this shouldNot containOnlyNulls()
fun <T> containOnlyNulls() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) =
      MatcherResult(
         value.all { it == null },
         "Sequence should contain only nulls",
         "Sequence should not contain only nulls"
      )
}

fun <T> Sequence<T>.shouldContainNull() = this should containNull()
fun <T> Sequence<T>.shouldNotContainNull() = this shouldNot containNull()
fun <T> containNull() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) =
      MatcherResult(
         value.any { it == null },
         "Sequence should contain at least one null",
         "Sequence should not contain any nulls"
      )
}

fun <T> Sequence<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)
fun <T> Sequence<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

fun <T, S : Sequence<T>> haveElementAt(index: Int, element: T) = object : Matcher<S> {
   override fun test(value: S) =
      MatcherResult(
         value.elementAt(index) == element,
         { "Sequence should contain $element at index $index" },
         { "Sequence should not contain $element at index $index" }
      )
}

fun <T> Sequence<T>.shouldContainNoNulls() = this should containNoNulls()
fun <T> Sequence<T>.shouldNotContainNoNulls() = this shouldNot containNoNulls()
fun <T> containNoNulls() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) =
      MatcherResult(
         value.all { it != null },
         { "Sequence should not contain nulls" },
         { "Sequence should have at least one null" }
      )
}

infix fun <T, C : Sequence<T>> C.shouldContain(t: T) = this should contain(t)
infix fun <T, C : Sequence<T>> C.shouldNotContain(t: T) = this shouldNot contain(t)
fun <T, C : Sequence<T>> contain(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.contains(t),
      { "Sequence should contain element $t" },
      { "Sequence should not contain element $t" }
   )
}

infix fun <T, C : Sequence<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)
fun <T, C : Sequence<T>> C?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)

infix fun <T, C : Sequence<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)
fun <T, C : Sequence<T>> C?.shouldContainExactly(vararg expected: T) = this should containExactly(*expected)

fun <T> containExactly(vararg expected: T): Matcher<Sequence<T>?> = containExactly(expected.asSequence())

/** Assert that a sequence contains exactly the given values and nothing else, in order. */
fun <T, C : Sequence<T>> containExactly(expected: C): Matcher<C?> = neverNullMatcher { value ->
   var passed: Boolean = value.count() == expected.count()
   var failMessage = "Sequence should contain exactly $expected but was $value"
   if (passed) {
      val diff = value.zip(expected) { a, b -> Triple(a, b, a == b) }.withIndex().find { !it.value.third }
      if (diff != null) {
         passed = false
         failMessage += " (expected ${diff.value.second} at ${diff.index} but found ${diff.value.first})"
      }
   } else {
      failMessage += " (expected ${expected.count()} elements but found ${value.count()})"
   }
   MatcherResult(
      passed,
      failMessage,
      "Sequence should not be exactly $expected"
   )
}

@Deprecated("use shouldNotContainAllInAnyOrder", ReplaceWith("shouldNotContainAllInAnyOrder"))
infix fun <T, C : Sequence<T>> C?.shouldNotContainExactlyInAnyOrder(expected: C) =
   this shouldNot containAllInAnyOrder(expected)

@Deprecated("use shouldNotContainAllInAnyOrder", ReplaceWith("shouldNotContainAllInAnyOrder"))
fun <T, C : Sequence<T>> C?.shouldNotContainExactlyInAnyOrder(vararg expected: T) =
   this shouldNot containAllInAnyOrder(*expected)

@Deprecated("use shouldContainAllInAnyOrder", ReplaceWith("shouldContainAllInAnyOrder"))
infix fun <T, C : Sequence<T>> C?.shouldContainExactlyInAnyOrder(expected: C) =
   this should containAllInAnyOrder(expected)

@Deprecated("use shouldContainAllInAnyOrder", ReplaceWith("shouldContainAllInAnyOrder"))
fun <T, C : Sequence<T>> C?.shouldContainExactlyInAnyOrder(vararg expected: T) =
   this should containAllInAnyOrder(*expected)

@Deprecated("use containAllInAnyOrder", ReplaceWith("containAllInAnyOrder"))
fun <T> containExactlyInAnyOrder(vararg expected: T): Matcher<Sequence<T>?> =
   containAllInAnyOrder(expected.asSequence())

@Deprecated("use containAllInAnyOrder", ReplaceWith("containAllInAnyOrder"))
/** Assert that a sequence contains the given values and nothing else, in any order. */
fun <T, C : Sequence<T>> containExactlyInAnyOrder(expected: C): Matcher<C?> = containAllInAnyOrder(expected)

infix fun <T, C : Sequence<T>> C?.shouldNotContainAllInAnyOrder(expected: C) =
   this shouldNot containAllInAnyOrder(expected)

fun <T, C : Sequence<T>> C?.shouldNotContainAllInAnyOrder(vararg expected: T) =
   this shouldNot containAllInAnyOrder(*expected)

infix fun <T, C : Sequence<T>> C?.shouldContainAllInAnyOrder(expected: C) =
   this should containAllInAnyOrder(expected)

fun <T, C : Sequence<T>> C?.shouldContainAllInAnyOrder(vararg expected: T) =
   this should containAllInAnyOrder(*expected)

fun <T> containAllInAnyOrder(vararg expected: T): Matcher<Sequence<T>?> =
   containAllInAnyOrder(expected.asSequence())

/** Assert that a sequence contains all the given values and nothing else, in any order. */
fun <T, C : Sequence<T>> containAllInAnyOrder(expected: C): Matcher<C?> = neverNullMatcher { value ->
   val passed = value.count() == expected.count() && expected.all { value.contains(it) }
   MatcherResult(
      passed,
      { "Sequence should contain the values of $expected in any order, but was $value" },
      { "Sequence should not contain the values of $expected in any order" }
   )
}

infix fun <T : Comparable<T>, C : Sequence<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)

fun <T : Comparable<T>, C : Sequence<T>> haveUpperBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      (value.max() ?: t) <= t,
      { "Sequence should have upper bound $t" },
      { "Sequence should not have upper bound $t" }
   )
}

infix fun <T : Comparable<T>, C : Sequence<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)

fun <T : Comparable<T>, C : Sequence<T>> haveLowerBound(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      (value.min() ?: t) >= t,
      { "Sequence should have lower bound $t" },
      { "Sequence should not have lower bound $t" }
   )
}

fun <T> Sequence<T>.shouldBeUnique() = this should beUnique()
fun <T> Sequence<T>.shouldNotBeUnique() = this shouldNot beUnique()
fun <T> beUnique() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.toSet().size == value.count(),
      { "Sequence should be Unique" },
      { "Sequence should contain at least one duplicate element" }
   )
}

fun <T> Sequence<T>.shouldContainDuplicates() = this should containDuplicates()
fun <T> Sequence<T>.shouldNotContainDuplicates() = this shouldNot containDuplicates()
fun <T> containDuplicates() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.toSet().size < value.count(),
      { "Sequence should contain duplicates" },
      { "Sequence should not contain duplicates" }
   )
}

fun <T : Comparable<T>> Sequence<T>.shouldBeSorted() = this should beSorted()
fun <T : Comparable<T>> Sequence<T>.shouldNotBeSorted() = this shouldNot beSorted()

fun <T : Comparable<T>> beSorted(): Matcher<Sequence<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      @Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
      val failure = value.zipWithNext().withIndex().firstOrNull { (i, it) -> it.first > it.second }
      val snippet = value.joinToString(",", limit = 10)
      val elementMessage = when (failure) {
         null -> ""
         else -> ". Element ${failure.value.first} at index ${failure.index} was greater than element ${failure.value.second}"
      }
      return MatcherResult(
         failure == null,
         { "Sequence $snippet should be sorted$elementMessage" },
         { "Sequence $snippet should not be sorted" }
      )
   }
}

infix fun <T> Sequence<T>.shouldBeSortedWith(comparator: Comparator<in T>) = this should beSortedWith(comparator)

infix fun <T> Sequence<T>.shouldBeSortedWith(cmp: (T, T) -> Int) = this should beSortedWith(cmp)

fun <T> beSortedWith(comparator: Comparator<in T>): Matcher<Sequence<T>> = sortedWith(comparator)

fun <T> beSortedWith(cmp: (T, T) -> Int): Matcher<Sequence<T>> = sortedWith(cmp)
fun <T> sortedWith(comparator: Comparator<in T>): Matcher<Sequence<T>> = sortedWith { a, b ->
   comparator.compare(a, b)
}

fun <T> sortedWith(cmp: (T, T) -> Int): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      @Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
      val failure = value.zipWithNext().withIndex().firstOrNull { (i, it) -> cmp(it.first, it.second) > 0 }
      val snippet = value.joinToString(",", limit = 10)
      val elementMessage = when (failure) {
         null -> ""
         else -> ". Element ${failure.value.first} at index ${failure.index} shouldn't precede element ${failure.value.second}"
      }
      return MatcherResult(
         failure == null,
         { "Sequence $snippet should be sorted$elementMessage" },
         { "Sequence $snippet should not be sorted" }
      )
   }
}

infix fun <T> Sequence<T>.shouldNotBeSortedWith(comparator: Comparator<in T>) = this shouldNot beSortedWith(comparator)
infix fun <T> Sequence<T>.shouldNotBeSortedWith(cmp: (T, T) -> Int) = this shouldNot beSortedWith(cmp)

infix fun <T> Sequence<T>.shouldHaveSingleElement(t: T) = this should singleElement(t)
infix fun <T> Sequence<T>.shouldNotHaveSingleElement(t: T) = this shouldNot singleElement(t)

fun <T> singleElement(t: T) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.count() == 1 && value.first() == t,
      { "Sequence should be a single element of $t but has ${value.count()} elements" },
      { "Sequence should not be a single element of $t" }
   )
}


infix fun <T> Sequence<T>.shouldHaveCount(count: Int) = this should haveCount(count)
infix fun <T> Sequence<T>.shouldNotHaveCount(count: Int) = this shouldNot haveCount(
   count)

infix fun <T> Sequence<T>.shouldHaveSize(size: Int) = this should haveCount(size)
infix fun <T> Sequence<T>.shouldNotHaveSize(size: Int) = this shouldNot haveCount(size)
//fun <T> haveSize(size: Int) = haveCount(size)
fun <T> haveSize(size: Int): Matcher<Sequence<T>> = haveCount(size)

fun <T> haveCount(count: Int): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) =
      MatcherResult(
         value.count() == count,
         { "Sequence should have count $count but has count ${value.count()}" },
         { "Sequence should not have count $count" }
      )
}


infix fun <T, U> Sequence<T>.shouldBeLargerThan(other: Sequence<U>) = this should beLargerThan(other)

fun <T, U> beLargerThan(other: Sequence<U>) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.count() > other.count(),
      { "Sequence of count ${value.count()} should be larger than sequence of count ${other.count()}" },
      { "Sequence of count ${value.count()} should not be larger than sequence of count ${other.count()}" }
   )
}

infix fun <T, U> Sequence<T>.shouldBeSmallerThan(other: Sequence<U>) = this should beSmallerThan(other)

fun <T, U> beSmallerThan(other: Sequence<U>) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.count() < other.count(),
      { "Sequence of count ${value.count()} should be smaller than sequence of count ${other.count()}" },
      { "Sequence of count ${value.count()} should not be smaller than sequence of count ${other.count()}" }
   )
}

infix fun <T, U> Sequence<T>.shouldBeSameCountAs(other: Sequence<U>) = this should beSameCountAs(
   other)

fun <T, U> beSameCountAs(other: Sequence<U>) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.count() == other.count(),
      { "Sequence of count ${value.count()} should be the same count as sequence of count ${other.count()}" },
      { "Sequence of count ${value.count()} should not be the same count as sequence of count ${other.count()}" }
   )
}

infix fun <T, U> Sequence<T>.shouldBeSameSizeAs(other: Sequence<U>) = this.shouldBeSameCountAs(other)

infix fun <T> Sequence<T>.shouldHaveAtLeastCount(n: Int) = this shouldHave atLeastCount(n)

fun <T> atLeastCount(n: Int) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.count() >= n,
      { "Sequence should contain at least $n elements" },
      { "Sequence should contain less than $n elements" }
   )
}

infix fun <T> Sequence<T>.shouldHaveAtLeastSize(n: Int) = this.shouldHaveAtLeastCount(n)

infix fun <T> Sequence<T>.shouldHaveAtMostCount(n: Int) = this shouldHave atMostCount(n)
fun <T> atMostCount(n: Int) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.count() <= n,
      { "Sequence should contain at most $n elements" },
      { "Sequence should contain more than $n elements" }
   )
}

infix fun <T> Sequence<T>.shouldHaveAtMostSize(n: Int) = this shouldHave atMostCount(n)


infix fun <T> Sequence<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>) = MatcherResult(
      value.any { p(it) },
      { "Sequence should contain an element that matches the predicate $p" },
      { "Sequence should not contain an element that matches the predicate $p" }
   )
}

fun <T : Comparable<T>> Sequence<T>.shouldContainInOrder(vararg ts: T) =
   this should containsInOrder(ts.asSequence())

infix fun <T : Comparable<T>> Sequence<T>.shouldContainInOrder(expected: Sequence<T>) =
   this should containsInOrder(expected)

fun <T : Comparable<T>> Sequence<T>.shouldNotContainInOrder(expected: Sequence<T>) =
   this shouldNot containsInOrder(expected)

/** Assert that a sequence contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: Sequence<T>): Matcher<Sequence<T>?> = neverNullMatcher { actual ->
   val subsequenceCount = subsequence.count()
   require(subsequenceCount > 0) { "expected values must not be empty" }

   var subsequenceIndex = 0
   val actualIterator = actual.iterator()

   while (actualIterator.hasNext() && subsequenceIndex < subsequenceCount) {
      if (actualIterator.next() == subsequence.elementAt(subsequenceIndex)) subsequenceIndex += 1
   }

   MatcherResult(
      subsequenceIndex == subsequence.count(),
      { "[$actual] did not contain the elements [$subsequence] in order" },
      { "[$actual] should not contain the elements [$subsequence] in order" }
   )
}

fun <T> Sequence<T>.shouldBeEmpty() = this should beEmpty()
fun <T> Sequence<T>.shouldNotBeEmpty() = this shouldNot beEmpty()
fun <T> beEmpty(): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult = MatcherResult(
      value.count() == 0,
      { "Sequence should be empty" },
      { "Sequence should not be empty" }
   )
}


fun <T> Sequence<T>.shouldContainAll(vararg ts: T) = this should containAll(ts.asSequence())
infix fun <T> Sequence<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts.asSequence())

infix fun <T> Sequence<T>.shouldContainAll(ts: Sequence<T>) = this should containAll(ts)
fun <T> Sequence<T>.shouldNotContainAll(vararg ts: T) = this shouldNot containAll(ts.asSequence())
infix fun <T> Sequence<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts.asSequence())

infix fun <T> Sequence<T>.shouldNotContainAll(ts: Sequence<T>) = this shouldNot containAll(ts)

fun <T, C : Sequence<T>> containAll(ts: C): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult = MatcherResult(
      ts.all { value.contains(it) },
      { "Sequence should contain all of ${value.joinToString(",", limit = 10)}" },
      { "Sequence should not contain all of ${value.joinToString(",", limit = 10)}" }
   )
}
