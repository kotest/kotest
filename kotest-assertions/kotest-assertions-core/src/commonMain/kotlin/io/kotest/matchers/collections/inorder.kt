package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(vararg ts: T): Matcher<Collection<T>?> = containsInOrder(ts.asList())

/** Assert that a collection contains a given subsequence, possibly with values in between. */
fun <T> containsInOrder(subsequence: List<T>): Matcher<Collection<T>?> = neverNullMatcher { actual ->
   require(subsequence.isNotEmpty()) { "expected values must not be empty" }

   var subsequenceIndex = 0
   val actualIterator = actual.iterator()

   while (actualIterator.hasNext() && subsequenceIndex < subsequence.size) {
      if (actualIterator.next() == subsequence[subsequenceIndex]) subsequenceIndex += 1
   }

   val mismatchDescription = if(subsequenceIndex == subsequence.size) "" else
      ", could not match element ${subsequence.elementAt(subsequenceIndex).print().value} at index $subsequenceIndex"

   val foundBeforeDescription = {
      if (subsequenceIndex == subsequence.size) "" else {
         val foundBeforeAtIndexes = actual.take(subsequenceIndex).mapIndexedNotNull { index, value ->
            if (value == subsequence[subsequenceIndex]) index else null
         }
         if (foundBeforeAtIndexes.isEmpty()) "" else {
            "\nbut found it before at index(es) ${foundBeforeAtIndexes.print().value}"
         }
      }
   }

   MatcherResult(
      subsequenceIndex == subsequence.size,
      { "${actual.print().value} did not contain the elements ${subsequence.print().value} in order$mismatchDescription${foundBeforeDescription()}" },
      { "${actual.print().value} should not contain the elements ${subsequence.print().value} in order" }
   )
}

fun <T> Iterable<T>.shouldContainInOrder(vararg ts: T) = toList().shouldContainInOrder(*ts)
fun <T> Array<T>.shouldContainInOrder(vararg ts: T) = asList().shouldContainInOrder(*ts)
fun <T> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
infix fun <T> Iterable<T>.shouldContainInOrder(expected: List<T>) = toList().shouldContainInOrder(expected)
infix fun <T> Array<T>.shouldContainInOrder(expected: List<T>) = asList().shouldContainInOrder(expected)
infix fun <T> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
infix fun <T> Iterable<T>.shouldNotContainInOrder(expected: Iterable<T>) = toList().shouldNotContainInOrder(expected.toList())
infix fun <T> Array<T>.shouldNotContainInOrder(expected: Array<T>) = asList().shouldNotContainInOrder(expected.asList())
infix fun <T> Iterable<T>.shouldNotContainInOrder(expected: List<T>) = toList().shouldNotContainInOrder(expected)
infix fun <T> Array<T>.shouldNotContainInOrder(expected: List<T>) = asList().shouldNotContainInOrder(expected)
infix fun <T> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

// BooleanArray
fun BooleanArray.shouldContainInOrder(vararg ts: Boolean): BooleanArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun BooleanArray.shouldNotContainInOrder(vararg ts: Boolean): BooleanArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// ByteArray
fun ByteArray.shouldContainInOrder(vararg ts: Byte): ByteArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun ByteArray.shouldNotContainInOrder(vararg ts: Byte): ByteArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// ShortArray
fun ShortArray.shouldContainInOrder(vararg ts: Short): ShortArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun ShortArray.shouldNotContainInOrder(vararg ts: Short): ShortArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// CharArray
fun CharArray.shouldContainInOrder(vararg ts: Char): CharArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun CharArray.shouldNotContainInOrder(vararg ts: Char): CharArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// IntArray
fun IntArray.shouldContainInOrder(vararg ts: Int): IntArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun IntArray.shouldNotContainInOrder(vararg ts: Int): IntArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// LongArray
fun LongArray.shouldContainInOrder(vararg ts: Long): LongArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun LongArray.shouldNotContainInOrder(vararg ts: Long): LongArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// FloatArray
fun FloatArray.shouldContainInOrder(vararg ts: Float): FloatArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun FloatArray.shouldNotContainInOrder(vararg ts: Float): FloatArray = apply { asList().shouldNotContainInOrder(ts.asList()) }

// DoubleArray
fun DoubleArray.shouldContainInOrder(vararg ts: Double): DoubleArray = apply { asList().shouldContainInOrder(ts.asList()) }
fun DoubleArray.shouldNotContainInOrder(vararg ts: Double): DoubleArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
