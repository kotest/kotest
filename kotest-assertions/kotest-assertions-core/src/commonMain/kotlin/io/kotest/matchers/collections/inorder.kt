package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.jvm.JvmName

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
@JvmName("shouldContainInOrderBooleanArray_infix")
infix fun BooleanArray.shouldContainInOrder(expected: BooleanArray): BooleanArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun BooleanArray.shouldNotContainInOrder(vararg ts: Boolean): BooleanArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderBooleanArray_infix")
infix fun BooleanArray.shouldNotContainInOrder(expected: BooleanArray): BooleanArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// ByteArray
fun ByteArray.shouldContainInOrder(vararg ts: Byte): ByteArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderByteArray_infix")
infix fun ByteArray.shouldContainInOrder(expected: ByteArray): ByteArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun ByteArray.shouldNotContainInOrder(vararg ts: Byte): ByteArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderByteArray_infix")
infix fun ByteArray.shouldNotContainInOrder(expected: ByteArray): ByteArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// ShortArray
fun ShortArray.shouldContainInOrder(vararg ts: Short): ShortArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderShortArray_infix")
infix fun ShortArray.shouldContainInOrder(expected: ShortArray): ShortArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun ShortArray.shouldNotContainInOrder(vararg ts: Short): ShortArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderShortArray_infix")
infix fun ShortArray.shouldNotContainInOrder(expected: ShortArray): ShortArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// CharArray
fun CharArray.shouldContainInOrder(vararg ts: Char): CharArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderCharArray_infix")
infix fun CharArray.shouldContainInOrder(expected: CharArray): CharArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun CharArray.shouldNotContainInOrder(vararg ts: Char): CharArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderCharArray_infix")
infix fun CharArray.shouldNotContainInOrder(expected: CharArray): CharArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// IntArray
fun IntArray.shouldContainInOrder(vararg ts: Int): IntArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderIntArray_infix")
infix fun IntArray.shouldContainInOrder(expected: IntArray): IntArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun IntArray.shouldNotContainInOrder(vararg ts: Int): IntArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderIntArray_infix")
infix fun IntArray.shouldNotContainInOrder(expected: IntArray): IntArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// LongArray
fun LongArray.shouldContainInOrder(vararg ts: Long): LongArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderLongArray_infix")
infix fun LongArray.shouldContainInOrder(expected: LongArray): LongArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun LongArray.shouldNotContainInOrder(vararg ts: Long): LongArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderLongArray_infix")
infix fun LongArray.shouldNotContainInOrder(expected: LongArray): LongArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// FloatArray
fun FloatArray.shouldContainInOrder(vararg ts: Float): FloatArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderFloatArray_infix")
infix fun FloatArray.shouldContainInOrder(expected: FloatArray): FloatArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun FloatArray.shouldNotContainInOrder(vararg ts: Float): FloatArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderFloatArray_infix")
infix fun FloatArray.shouldNotContainInOrder(expected: FloatArray): FloatArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// DoubleArray
fun DoubleArray.shouldContainInOrder(vararg ts: Double): DoubleArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderDoubleArray_infix")
infix fun DoubleArray.shouldContainInOrder(expected: DoubleArray): DoubleArray = apply { asList().shouldContainInOrder(expected.asList()) }
fun DoubleArray.shouldNotContainInOrder(vararg ts: Double): DoubleArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderDoubleArray_infix")
infix fun DoubleArray.shouldNotContainInOrder(expected: DoubleArray): DoubleArray = apply { asList().shouldNotContainInOrder(expected.asList()) }
