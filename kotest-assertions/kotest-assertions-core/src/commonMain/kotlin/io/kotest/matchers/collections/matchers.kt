package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

@IgnorableReturnValue
fun <T> Iterable<T>.shouldHaveElementAt(index: Int, element: T) = toList().shouldHaveElementAt(index, element)
@IgnorableReturnValue
fun <T> Array<T>.shouldHaveElementAt(index: Int, element: T) = asList().shouldHaveElementAt(index, element)
@IgnorableReturnValue
fun <T> List<T>.shouldHaveElementAt(index: Int, element: T) = this should haveElementAt(index, element)

@IgnorableReturnValue
fun <T> Iterable<T>.shouldNotHaveElementAt(index: Int, element: T) = toList().shouldNotHaveElementAt(index, element)
@IgnorableReturnValue
fun <T> Array<T>.shouldNotHaveElementAt(index: Int, element: T) = asList().shouldNotHaveElementAt(index, element)
@IgnorableReturnValue
fun <T> List<T>.shouldNotHaveElementAt(index: Int, element: T) = this shouldNot haveElementAt(index, element)

fun <T, L : List<T>> haveElementAt(index: Int, element: T) = object : Matcher<L> {
   override fun test(value: L): MatcherResult {
      val passed = index in value.indices && value[index] == element
      val invalidIndexMsg = when {
         index < 0 -> "But index $index is negative"
         index < value.size -> ""
         else -> "But it is too short: only ${value.size} elements"
      }
      val unexpectedElementMsg = when {
         passed -> ""
         index in value.indices -> "Expected: <${element.print().value}>, but was <${value[index].print().value}>"
         else -> ""
      }
      val indexesForElement = value.mapIndexedNotNull { index, current ->
         if(current == element) index else null
      }
      val indexesForElementMsg = if(passed || indexesForElement.isEmpty())
         ""
      else "Element was found at index(es): ${indexesForElement.print().value}"
      val additionalDescriptions = listOf(invalidIndexMsg, unexpectedElementMsg, indexesForElementMsg).filter {
         it.isNotEmpty()
      }
      val additionalDescriptionsMsg = if(additionalDescriptions.isEmpty()) ""
      else "\n${additionalDescriptions.joinToString("\n")}"
      return MatcherResult(
         passed,
         { "Collection ${value.print().value} should contain ${element.print().value} at index $index$additionalDescriptionsMsg" },
         { "Collection ${value.print().value} should not contain ${element.print().value} at index $index" }
      )
   }
}

@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldExist(p: (T) -> Boolean) = toList().shouldExist(p)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldExist(p: (T) -> Boolean) = asList().shouldExist(p)
@IgnorableReturnValue
infix fun <T> Collection<T>.shouldExist(p: (T) -> Boolean) = this should exist(p)
fun <T> exist(p: (T) -> Boolean) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val matchingElementsIndexes = value.mapIndexedNotNull { index, element ->
         if(p(element)) index else null
      }
      return MatcherResult(
         matchingElementsIndexes.isNotEmpty(),
         { "Collection ${value.print().value} should contain an element that matches the predicate" },
         { "Collection ${value.print().value} should not contain an element that matches the predicate, but elements with the following indexes matched: ${matchingElementsIndexes.print().value}" }
      )
   }
}

@IgnorableReturnValue
fun <T> Iterable<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = toList().shouldMatchInOrder(assertions.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = asList().shouldMatchInOrder(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = this.shouldMatchInOrder(assertions.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = toList().shouldMatchInOrder(assertions)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = asList().shouldMatchInOrder(assertions)
@IgnorableReturnValue
infix fun <T> List<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = this should matchInOrder(assertions.toList(), allowGaps = false)
@IgnorableReturnValue
fun <T> Iterable<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = toList().shouldNotMatchInOrder(assertions.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = asList().shouldNotMatchInOrder(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = this.shouldNotMatchInOrder(assertions.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = toList().shouldNotMatchInOrder(assertions)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = asList().shouldNotMatchInOrder(assertions)
@IgnorableReturnValue
infix fun <T> List<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = this shouldNot matchInOrder(assertions.toList(), allowGaps = false)

@IgnorableReturnValue
fun <T> Iterable<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = toList().shouldMatchInOrderSubset(assertions.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = asList().shouldMatchInOrderSubset(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = this.shouldMatchInOrderSubset(assertions.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = toList().shouldMatchInOrderSubset(assertions)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = asList().shouldMatchInOrderSubset(assertions)
@IgnorableReturnValue
infix fun <T> List<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = this should matchInOrder(assertions.toList(), allowGaps = true)
@IgnorableReturnValue
fun <T> Iterable<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = toList().shouldNotMatchInOrderSubset(assertions.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = asList().shouldNotMatchInOrderSubset(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = this.shouldNotMatchInOrderSubset(assertions.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = toList().shouldNotMatchInOrderSubset(assertions)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = asList().shouldNotMatchInOrderSubset(assertions)
@IgnorableReturnValue
infix fun <T> List<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = this shouldNot matchInOrder(assertions.toList(), allowGaps = true)

@IgnorableReturnValue
fun <T> Iterable<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = toList().shouldMatchEach(assertions.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = asList().shouldMatchEach(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = this.shouldMatchEach(assertions.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = toList().shouldMatchEach(assertions)
@IgnorableReturnValue
fun <T> Iterable<T>.shouldMatchEach(expected: Iterable<T>, asserter: (T, T) -> Unit) = toList().shouldMatchEach(expected.toList(), asserter)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = asList().shouldMatchEach(assertions)
@IgnorableReturnValue
infix fun <T> List<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = this should matchEach(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldMatchEach(expected: List<T>, asserter: (T, T) -> Unit) = this should matchEach(expected, asserter)
@IgnorableReturnValue
fun <T> Iterable<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = toList().shouldNotMatchEach(assertions.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = asList().shouldNotMatchEach(assertions.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = this.shouldNotMatchEach(assertions.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = toList().shouldNotMatchEach(assertions)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = asList().shouldNotMatchEach(assertions)
@IgnorableReturnValue
infix fun <T> List<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = this shouldNot matchEach(assertions.toList())

@IgnorableReturnValue
fun <T> Iterable<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = toList().shouldExistInOrder(ps.toList())
@IgnorableReturnValue
fun <T> Array<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = asList().shouldExistInOrder(ps.toList())
@IgnorableReturnValue
fun <T> List<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = this.shouldExistInOrder(ps.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = toList().shouldExistInOrder(expected)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = asList().shouldExistInOrder(expected)
@IgnorableReturnValue
infix fun <T> List<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = this should existInOrder(expected)
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotExistInOrder(expected: Iterable<(T) -> Boolean>) =
   toList().shouldNotExistInOrder(expected.toList())

@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotExistInOrder(expected: Array<(T) -> Boolean>) =
   asList().shouldNotExistInOrder(expected.asList())

@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) =
   toList().shouldNotExistInOrder(expected)

@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = asList().shouldNotExistInOrder(expected)
@IgnorableReturnValue
infix fun <T> List<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = this shouldNot existInOrder(expected)

@IgnorableReturnValue
fun <T> Iterable<T>.shouldContainAnyOf(vararg ts: T) = toList().shouldContainAnyOf(*ts)
@IgnorableReturnValue
fun <T> Array<T>.shouldContainAnyOf(vararg ts: T) = asList().shouldContainAnyOf(*ts)
@IgnorableReturnValue
fun <T> Collection<T>.shouldContainAnyOf(vararg ts: T) = this should containAnyOf(ts.asList())
@IgnorableReturnValue
fun <T> Iterable<T>.shouldNotContainAnyOf(vararg ts: T) = toList().shouldNotContainAnyOf(*ts)
@IgnorableReturnValue
fun <T> Array<T>.shouldNotContainAnyOf(vararg ts: T) = asList().shouldNotContainAnyOf(*ts)
@IgnorableReturnValue
fun <T> Collection<T>.shouldNotContainAnyOf(vararg ts: T) = this shouldNot containAnyOf(ts.asList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldContainAnyOf(ts: Collection<T>) = toList().shouldContainAnyOf(ts)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldContainAnyOf(ts: Collection<T>) = asList().shouldContainAnyOf(ts)
@IgnorableReturnValue
infix fun <T> Collection<T>.shouldContainAnyOf(ts: Collection<T>) = this should containAnyOf(ts)
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotContainAnyOf(ts: Collection<T>) = toList().shouldNotContainAnyOf(ts)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotContainAnyOf(ts: Collection<T>) = asList().shouldNotContainAnyOf(ts)
@IgnorableReturnValue
infix fun <T> Collection<T>.shouldNotContainAnyOf(ts: Collection<T>) = this shouldNot containAnyOf(ts)

fun <T> containAnyOf(ts: Collection<T>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult {
      val elementsInValue = value.mapIndexedNotNull { index, t -> if(ts.contains(t)) IndexedValue(index, t) else null }
      return MatcherResult(
         elementsInValue.isNotEmpty(),
         { "Collection ${value.print().value} should contain any of ${ts.print().value}" },
         { "Collection ${value.print().value} should not contain any of ${ts.print().value}${describeForbiddenElementsInCollection(elementsInValue)}" }
      )
   }
}

internal fun<T> describeForbiddenElementsInCollection(indexedElements: List<IndexedValue<T>>): String {
   return "\nForbidden elements found in collection:\n${indexedElements.joinToString("\n") {
      indexedValue -> "[${indexedValue.index}] => ${indexedValue.value.print().value}"
   } }"
}
