package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.assertions.similarity.possibleMatchesDescription
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun BooleanArray.shouldContainExactCopies(element: Boolean, copies: Int): BooleanArray = apply { asList().shouldContainExactCopies(element, copies) }
fun BooleanArray.shouldNotContainExactCopies(element: Boolean, copies: Int): BooleanArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun ByteArray.shouldContainExactCopies(element: Byte, copies: Int): ByteArray = apply { asList().shouldContainExactCopies(element, copies) }
fun ByteArray.shouldNotContainExactCopies(element: Byte, copies: Int): ByteArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun ShortArray.shouldContainExactCopies(element: Short, copies: Int): ShortArray = apply { asList().shouldContainExactCopies(element, copies) }
fun ShortArray.shouldNotContainExactCopies(element: Short, copies: Int): ShortArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun CharArray.shouldContainExactCopies(element: Char, copies: Int): CharArray = apply { asList().shouldContainExactCopies(element, copies) }
fun CharArray.shouldNotContainExactCopies(element: Char, copies: Int): CharArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun IntArray.shouldContainExactCopies(element: Int, copies: Int): IntArray = apply { asList().shouldContainExactCopies(element, copies) }
fun IntArray.shouldNotContainExactCopies(element: Int, copies: Int): IntArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun LongArray.shouldContainExactCopies(element: Long, copies: Int): LongArray = apply { asList().shouldContainExactCopies(element, copies) }
fun LongArray.shouldNotContainExactCopies(element: Long, copies: Int): LongArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun FloatArray.shouldContainExactCopies(element: Float, copies: Int): FloatArray = apply { asList().shouldContainExactCopies(element, copies) }
fun FloatArray.shouldNotContainExactCopies(element: Float, copies: Int): FloatArray = apply { asList().shouldNotContainExactCopies(element, copies) }
fun DoubleArray.shouldContainExactCopies(element: Double, copies: Int): DoubleArray = apply { asList().shouldContainExactCopies(element, copies) }
fun DoubleArray.shouldNotContainExactCopies(element: Double, copies: Int): DoubleArray = apply { asList().shouldNotContainExactCopies(element, copies) }

/**
 * Verifies that this element is not in [array] by comparing value in
 * exactly the specified number of copies
 *
 * An empty collection will always fail.
 *
 * @see [containExactCopies]
 */
fun <T, I : Iterable<T>> I.shouldNotContainExactCopies(element: T, copies: Int): I = apply {
   toList() shouldNot containExactCopies(element, copies)
}

/**
 * Verifies that this element is not in [array] by comparing value in
 * exactly the specified number of copies
 *
 * An empty collection will always fail.
 *
 * @see [containExactCopies]
 */
fun <T> Array<T>.shouldNotContainExactCopies(element: T, copies: Int): Array<T> = apply {
   asList().shouldNotContainExactCopies(element, copies)
}

/**
 * Verifies that this element is in [collection] by comparing value,
 * exactly the specified number of copies
 *
 * Assertion to check that this element is in [collection]. This assertion checks by value, and not by reference,
 * so even if the exact instance is not in [collection] but other instances with same value are present,
 * in the exact amount, the test will pass.
 *
 * An empty collection will always fail.
 *
 * @see [containExactCopies]
 */

fun <T, I : Iterable<T>> I.shouldContainExactCopies(element: T, copies: Int): I = apply {
   toList() should containExactCopies(element, copies)
}

/**
 * Verifies that this element is in [array] by comparing value,
 * exactly the specified number of copies
 *
 * Assertion to check that this element is in [array]. This assertion checks by value, and not by reference,
 * so even if the exact instance is not in [array] but other instances with same value are present,
 * in the exact amount, the test will pass.
 *
 * An empty collection will always fail.
 *
 * @see [containExactCopies]
 */
fun <T> Array<T>.shouldContainExactCopies(element: T, copies: Int): Array<T> = apply {
   asList().shouldContainExactCopies(element, copies)
}
fun <T, C : Collection<T>> containExactCopies(element: T, copies: Int) = object : Matcher<C> {
   override fun test(value: C) : MatcherResult {
      require(copies > 0) { "Copies should be positive, was $copies" }
      val passedAtIndexes = value.mapIndexedNotNull {
            index, it -> if(it == element) index else null
      }
      val passed = passedAtIndexes.size == copies
      val possibleMatches = {
         if (!passed) {
            val candidates = possibleMatchesDescription(value.toSet(), element)
            if (candidates.isEmpty()) "" else "\nPossibleMatches:$candidates"
         } else ""
      }
      return MatcherResult(
         passed,
         {
            "Collection should contain $copies copies of element ${element.print().value}; " +
               "but contained ${passedAtIndexes.size} copies ${if(passedAtIndexes.size > 0) "at index(es) ${passedAtIndexes.print().value}, and " else "but "}" +
               "the collection is ${value.print().value}${possibleMatches()}"
         },
         { "Collection should not contain $copies copies of element ${element.print().value}, but it did at index(es):${passedAtIndexes.print().value}" }
      )
   }
}
