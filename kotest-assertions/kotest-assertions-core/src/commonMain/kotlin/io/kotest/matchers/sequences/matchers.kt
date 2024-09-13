@file:Suppress("unused")

package io.kotest.matchers.sequences

import io.kotest.assertions.eq.eq
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.duplicates
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot
import kotlin.collections.indexOfFirst

/*
How should infinite sequences be detected, and how should they be dealt with?

Sequence<T>.count() may run through the whole sequence (sequences from `generateSequence()` do so), so isn't always a safe way to detect infinite sequences.

For now, the documentation should mention that infinite sequences will cause these matchers never to halt.
*/

fun <T> Sequence<T>.shouldContainOnlyNulls() = this should containOnlyNulls()
fun <T> Sequence<T>.shouldNotContainOnlyNulls() = this shouldNot containOnlyNulls()
fun <T> containOnlyNulls() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val firstNotNull = value.mapIndexed { index, t ->  index to t}.firstOrNull { it.second != null }
      return MatcherResult(
         firstNotNull == null,
         { "Sequence should contain only nulls, but had a non-null element ${firstNotNull!!.second.print().value} at index ${firstNotNull.first}" },
         { "Sequence should not contain only nulls" }
      )
   }
}

fun <T> Sequence<T>.shouldContainNull() = this should containNull()
fun <T> Sequence<T>.shouldNotContainNull() = this shouldNot containNull()
fun <T> containNull() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val indexOfFirstNull = value.indexOfFirst { it == null }
      return MatcherResult(
         indexOfFirstNull != -1,
         { "Sequence should contain at least one null" },
         { "Sequence should not contain any nulls, but contained at least one at index $indexOfFirstNull" }
      )
   }
}

fun <T> Sequence<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)
fun <T> Sequence<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

fun <T, S : Sequence<T>> haveElementAt(index: Int, element: T) = object : Matcher<S> {
   override fun test(value: S): MatcherResult {
      val sequenceHead = value.take(index + 1).toList()
      val elementAtIndex = sequenceHead.elementAtOrNull(index)
      val passed = elementAtIndex == element
      val description = when{
         passed -> ""
         elementAtIndex != null && elementAtIndex != element -> ", but the value was different: ${elementAtIndex.print().value}."
         else -> ", but the sequence only had ${sequenceHead.size} elements"
      }
      return MatcherResult(
         passed,
         { "Sequence should contain ${element.print().value} at index $index$description" },
         { "Sequence should not contain ${element.print().value} at index $index" }
      )
   }
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
   override fun test(value: C): MatcherResult {
      val indexOfElement = value.indexOfFirst { it == t }
      return MatcherResult(
         indexOfElement >= 0,
         { "Sequence should contain element $t" },
         { "Sequence should not contain element ${t.print().value}, but contained it at index $indexOfElement" }
      )
   }
}

infix fun <T, C : Sequence<T>> C?.shouldNotContainExactly(expected: C) = this shouldNot containExactly(expected)
fun <T, C : Sequence<T>> C?.shouldNotContainExactly(vararg expected: T) = this shouldNot containExactly(*expected)

infix fun <T, C : Sequence<T>> C?.shouldContainExactly(expected: C) = this should containExactly(expected)
fun <T, C : Sequence<T>> C?.shouldContainExactly(vararg expected: T) = this should containExactly(*expected)

fun <T> containExactly(vararg expected: T): Matcher<Sequence<T>?> = containExactly(expected.asSequence())

/** Assert that a sequence contains exactly the given values and nothing else, in order. */
fun <T, C : Sequence<T>> containExactly(expected: C): Matcher<C?> = neverNullMatcher { value ->
   val actualIterator = value.withIndex().iterator()
   val expectedIterator = expected.withIndex().iterator()
   val consumedActualValues = mutableListOf<IndexedValue<T>>()
   val consumedExpectedValues = mutableListOf<IndexedValue<T>>()

   fun IndexedValue<T>.printValue() = this.value.print().value
   fun List<IndexedValue<T>>.printValues(hasNext: Boolean) =
      joinToString(postfix = if (hasNext) ", ..." else "") { it.printValue() }

   var passed = true
   var failDetails = ""
   while (passed && actualIterator.hasNext() && expectedIterator.hasNext()) {
      val actualElement = actualIterator.next()
      consumedActualValues.add(actualElement)
      val expectedElement = expectedIterator.next()
      consumedExpectedValues.add(expectedElement)
      if (eq(actualElement.value, expectedElement.value) != null) {
         failDetails =
            "\nExpected ${expectedElement.printValue()} at index ${expectedElement.index} but found ${actualElement.printValue()}."
         passed = false
      }
   }

   if (passed && actualIterator.hasNext()) {
      val actualElement = actualIterator.next()
      consumedActualValues.add(actualElement)
      failDetails =
         "\nActual sequence has more elements than expected sequence: found ${actualElement.printValue()} at index ${actualElement.index}."
      passed = false
   }

   if (passed && expectedIterator.hasNext()) {
      val expectedElement = expectedIterator.next()
      consumedExpectedValues.add(expectedElement)
      failDetails =
         "\nActual sequence has less elements than expected sequence: expected ${expectedElement.printValue()} at index ${expectedElement.index}."
      passed = false
   }

   MatcherResult(
      passed,
      {
         "Sequence should contain exactly ${consumedExpectedValues.printValues(expectedIterator.hasNext())} but was ${
            consumedActualValues.printValues(actualIterator.hasNext())
         }.$failDetails"
      },
      { "Sequence should not contain exactly ${consumedExpectedValues.printValues(expectedIterator.hasNext())}" })
}

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
   val valueAsList = value.toList()
   val expectedAsList = expected.toList()
   val comparison = UnorderedCollectionsDifference.of(expectedAsList, valueAsList)
   MatcherResult(
      comparison.isMatch(),
      { "Sequence should contain the values of $expectedAsList in any order, but was $valueAsList.${comparison}" },
      { "Sequence should not contain the values of $expectedAsList in any order" }
   )
}

internal fun <T> List<T>.counted(): Map<T, Int> = this.groupingBy { it }.eachCount()

infix fun <T : Comparable<T>, C : Sequence<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)

fun <T : Comparable<T>, C : Sequence<T>> haveUpperBound(t: T) = object : Matcher<C> {
   override fun test(value: C): MatcherResult {
      val elementAboveUpperBound = value.withIndex().firstOrNull { it.value > t }
      val elementAboveUpperBoundStr = elementAboveUpperBound?.let {
         ", but element at index ${it.index} was: ${it.value.print().value}"
      } ?: ""
      return MatcherResult(
         elementAboveUpperBound == null,
         { "Sequence should have upper bound $t$elementAboveUpperBoundStr" },
         { "Sequence should not have upper bound $t" }
      )
   }
}

infix fun <T : Comparable<T>, C : Sequence<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)

fun <T : Comparable<T>, C : Sequence<T>> haveLowerBound(t: T) = object : Matcher<C> {
   override fun test(value: C): MatcherResult {
      val elementBelowLowerBound = value.withIndex().firstOrNull { it.value < t }
      val elementBelowLowerBoundStr = elementBelowLowerBound?.let {
         ", but element at index ${it.index} was: ${it.value.print().value}"
      } ?: ""
      return MatcherResult(
         elementBelowLowerBound == null,
         { "Sequence should have lower bound $t$elementBelowLowerBoundStr" },
         { "Sequence should not have lower bound $t" }
      )
   }
}

fun <T> Sequence<T>.shouldBeUnique() = this should beUnique()
fun <T> Sequence<T>.shouldNotBeUnique() = this shouldNot beUnique()
fun <T> beUnique() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val duplicates = value.toList().duplicates()
      return MatcherResult(
         duplicates.isEmpty(),
         { "Sequence should be Unique, but has duplicates: ${duplicates.print().value}" },
         { "Sequence should contain at least one duplicate element" }
      )
   }
}

fun <T> Sequence<T>.shouldContainDuplicates() = this should containDuplicates()
fun <T> Sequence<T>.shouldNotContainDuplicates() = this shouldNot containDuplicates()
fun <T> containDuplicates() = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val duplicates = value.toList().duplicates()
      return MatcherResult(
         duplicates.isNotEmpty(),
         { "Sequence should contain duplicates" },
         { "Sequence should not contain duplicates, but has some: ${duplicates.print().value}" }
      )
   }
}

fun <T : Comparable<T>> Sequence<T>.shouldBeSorted() = this should beSorted()
fun <T : Comparable<T>> Sequence<T>.shouldNotBeSorted() = this shouldNot beSorted()

fun <T : Comparable<T>> beSorted(): Matcher<Sequence<T>> = sorted()
fun <T : Comparable<T>> sorted(): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val valueAsList = value.toList()
      val failure = valueAsList.zipWithNext().withIndex().firstOrNull { (_, it) -> it.first > it.second }
      val snippet = valueAsList.joinToString(",", limit = 10)
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
      val valueAsList = value.toList()
      val failure = valueAsList.zipWithNext().withIndex().firstOrNull { (_, it) -> cmp(it.first, it.second) > 0 }
      val snippet = valueAsList.joinToString(",", limit = 10)
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

fun <T> singleElement(expectedElement: T) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      var failureMessage: String? = null
      val iterator = value.iterator()
      var actualElement: T?
      if (!iterator.hasNext()) {
         failureMessage = "Sequence should have a single element of $expectedElement but is empty."
      } else if (eq(iterator.next().also { actualElement = it }, expectedElement) != null) {
         failureMessage =
            "Sequence should have a single element of $expectedElement but has $actualElement as first element."
      } else if (iterator.hasNext()) {
         failureMessage = "Sequence should have a single element of $expectedElement but has more than one element."
      }
      return MatcherResult(
         failureMessage == null,
         { failureMessage ?: "" },
         { "Sequence should not have a single element of $expectedElement." }
      )
   }
}


infix fun <T> Sequence<T>.shouldHaveCount(count: Int) = this should haveCount(count)
infix fun <T> Sequence<T>.shouldNotHaveCount(count: Int) = this shouldNot haveCount(
   count
)

infix fun <T> Sequence<T>.shouldHaveSize(size: Int) = this should haveCount(size)
infix fun <T> Sequence<T>.shouldNotHaveSize(size: Int) = this shouldNot haveCount(size)

//fun <T> haveSize(size: Int) = haveCount(size)
fun <T> haveSize(size: Int): Matcher<Sequence<T>> = haveCount(size)

fun <T> haveCount(count: Int): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val actualCount = value.count()
      return MatcherResult(
         actualCount == count,
         { "Sequence should have count $count but has count $actualCount" },
         { "Sequence should not have count $count" }
      )
   }
}


infix fun <T, U> Sequence<T>.shouldBeLargerThan(other: Sequence<U>) = this should beLargerThan(other)

fun <T, U> beLargerThan(other: Sequence<U>) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val actualCount = value.count()
      val expectedCount = other.count()
      return MatcherResult(
         actualCount > expectedCount,
         { "Sequence of count $actualCount should be larger than sequence of count $expectedCount" },
         { "Sequence of count $actualCount should not be larger than sequence of count $expectedCount" }
      )
   }
}

infix fun <T, U> Sequence<T>.shouldBeSmallerThan(other: Sequence<U>) = this should beSmallerThan(other)

fun <T, U> beSmallerThan(other: Sequence<U>) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val actualCount = value.count()
      val expectedCount = other.count()
      return MatcherResult(
         actualCount < expectedCount,
         { "Sequence of count $actualCount should be smaller than sequence of count $expectedCount" },
         { "Sequence of count $actualCount should not be smaller than sequence of count $expectedCount" }
      )
   }
}

infix fun <T, U> Sequence<T>.shouldBeSameCountAs(other: Sequence<U>) = this should beSameCountAs(
   other
)

fun <T, U> beSameCountAs(other: Sequence<U>) = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {
      val actualCount = value.count()
      val expectedCount = other.count()
      return MatcherResult(
         actualCount == expectedCount,
         { "Sequence of count $actualCount should be the same count as sequence of count $expectedCount" },
         { "Sequence of count $actualCount should not be the same count as sequence of count $expectedCount" }
      )
   }
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

fun <T : Comparable<T>> Sequence<T>.shouldContainInOrder(vararg ts: T) =
   this should containsInOrder(ts.asSequence())

infix fun <T : Comparable<T>> Sequence<T>.shouldContainInOrder(expected: Sequence<T>) =
   this should containsInOrder(expected)

fun <T : Comparable<T>> Sequence<T>.shouldNotContainInOrder(expected: Sequence<T>) =
   this shouldNot containsInOrder(expected)

/** Assert that a sequence contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: Sequence<T>): Matcher<Sequence<T>?> = neverNullMatcher { actual ->
   val subsequenceAsList = subsequence.toList()
   require(subsequenceAsList.isNotEmpty()) { "expected values must not be empty" }

   var subsequenceIndex = 0
   val actualIterator = actual.iterator()

   while (actualIterator.hasNext() && subsequenceIndex < subsequenceAsList.size) {
      if (actualIterator.next() == subsequenceAsList.elementAt(subsequenceIndex)) subsequenceIndex += 1
   }

   val mismatchDescription = if (subsequenceIndex == subsequenceAsList.size) "" else
      ", could not match element ${subsequenceAsList.elementAt(subsequenceIndex)} at index $subsequenceIndex"

   MatcherResult(
      subsequenceIndex == subsequenceAsList.size,
      { "[$actual] did not contain the elements [$subsequenceAsList] in order$mismatchDescription" },
      { "[$actual] should not contain the elements [$subsequenceAsList] in order" }
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


fun <T> Sequence<T>.shouldContainAll(vararg ts: T) = this should containAll(ts.toList())
infix fun <T> Sequence<T>.shouldContainAll(ts: Collection<T>) = this should containAll(ts.toList())
infix fun <T> Sequence<T>.shouldContainAll(ts: Sequence<T>) = this should containAll(ts)

fun <T> Sequence<T>.shouldNotContainAll(vararg ts: T) = this shouldNot containAll(ts.asList())
infix fun <T> Sequence<T>.shouldNotContainAll(ts: Collection<T>) = this shouldNot containAll(ts.toList())
infix fun <T> Sequence<T>.shouldNotContainAll(ts: Sequence<T>) = this shouldNot containAll(ts)

fun <T> containAll(ts: Sequence<T>): Matcher<Sequence<T>> = containAll(ts.toList())
fun <T> containAll(ts: List<T>): Matcher<Sequence<T>> = object : Matcher<Sequence<T>> {
   override fun test(value: Sequence<T>): MatcherResult {

      val remaining = ts.toMutableSet()
      val iter = value.iterator()
      while (remaining.isNotEmpty() && iter.hasNext()) {
         remaining.remove(iter.next())
      }

      val failure =
         { "Sequence should contain all of ${ts.print().value} but was missing ${remaining.print().value}" }
      val negFailure = { "Sequence should not contain all of ${ts.print().value}" }

      return MatcherResult(remaining.isEmpty(), failure, negFailure)
   }
}

fun <T> Sequence<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = toList().shouldMatchEach(assertions.toList())
infix fun <T> Sequence<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = toList().shouldMatchEach(assertions)
fun <T> Sequence<T>.shouldMatchEach(expected: Sequence<T>, asserter: (T, T) -> Unit) =
   toList().shouldMatchEach(expected.toList(), asserter)
