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
         val foundBeforeAtIndexes = actual.mapIndexedNotNull { index, value ->
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

@IgnorableReturnValue
fun <T> Iterable<T>.shouldContainInOrder(vararg ts: T) = toList().shouldContainInOrder(*ts)
@IgnorableReturnValue
fun <T> Array<T>.shouldContainInOrder(vararg ts: T) = asList().shouldContainInOrder(*ts)
@IgnorableReturnValue
fun <T> List<T>.shouldContainInOrder(vararg ts: T) = this.shouldContainInOrder(ts.toList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldContainInOrder(expected: List<T>) = toList().shouldContainInOrder(expected)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldContainInOrder(expected: List<T>) = asList().shouldContainInOrder(expected)
@IgnorableReturnValue
infix fun <T> List<T>.shouldContainInOrder(expected: List<T>) = this should containsInOrder(expected)
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotContainInOrder(expected: Iterable<T>) = toList().shouldNotContainInOrder(expected.toList())
@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotContainInOrder(expected: Array<T>) = asList().shouldNotContainInOrder(expected.asList())
@IgnorableReturnValue
infix fun <T> Iterable<T>.shouldNotContainInOrder(expected: List<T>) = toList().shouldNotContainInOrder(expected)
@IgnorableReturnValue
infix fun <T> Array<T>.shouldNotContainInOrder(expected: List<T>) = asList().shouldNotContainInOrder(expected)
@IgnorableReturnValue
infix fun <T> List<T>.shouldNotContainInOrder(expected: List<T>) = this shouldNot containsInOrder(expected)

// BooleanArray
@IgnorableReturnValue
fun BooleanArray.shouldContainInOrder(vararg ts: Boolean): BooleanArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderBooleanArray_infix")
@IgnorableReturnValue
infix fun BooleanArray.shouldContainInOrder(expected: BooleanArray): BooleanArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun BooleanArray.shouldNotContainInOrder(vararg ts: Boolean): BooleanArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderBooleanArray_infix")
@IgnorableReturnValue
infix fun BooleanArray.shouldNotContainInOrder(expected: BooleanArray): BooleanArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// ByteArray
@IgnorableReturnValue
fun ByteArray.shouldContainInOrder(vararg ts: Byte): ByteArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderByteArray_infix")
@IgnorableReturnValue
infix fun ByteArray.shouldContainInOrder(expected: ByteArray): ByteArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun ByteArray.shouldNotContainInOrder(vararg ts: Byte): ByteArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderByteArray_infix")
@IgnorableReturnValue
infix fun ByteArray.shouldNotContainInOrder(expected: ByteArray): ByteArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// ShortArray
@IgnorableReturnValue
fun ShortArray.shouldContainInOrder(vararg ts: Short): ShortArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderShortArray_infix")
@IgnorableReturnValue
infix fun ShortArray.shouldContainInOrder(expected: ShortArray): ShortArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun ShortArray.shouldNotContainInOrder(vararg ts: Short): ShortArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderShortArray_infix")
@IgnorableReturnValue
infix fun ShortArray.shouldNotContainInOrder(expected: ShortArray): ShortArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// CharArray
@IgnorableReturnValue
fun CharArray.shouldContainInOrder(vararg ts: Char): CharArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderCharArray_infix")
@IgnorableReturnValue
infix fun CharArray.shouldContainInOrder(expected: CharArray): CharArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun CharArray.shouldNotContainInOrder(vararg ts: Char): CharArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderCharArray_infix")
@IgnorableReturnValue
infix fun CharArray.shouldNotContainInOrder(expected: CharArray): CharArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// IntArray
@IgnorableReturnValue
fun IntArray.shouldContainInOrder(vararg ts: Int): IntArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderIntArray_infix")
@IgnorableReturnValue
infix fun IntArray.shouldContainInOrder(expected: IntArray): IntArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun IntArray.shouldNotContainInOrder(vararg ts: Int): IntArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderIntArray_infix")
@IgnorableReturnValue
infix fun IntArray.shouldNotContainInOrder(expected: IntArray): IntArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// LongArray
@IgnorableReturnValue
fun LongArray.shouldContainInOrder(vararg ts: Long): LongArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderLongArray_infix")
@IgnorableReturnValue
infix fun LongArray.shouldContainInOrder(expected: LongArray): LongArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun LongArray.shouldNotContainInOrder(vararg ts: Long): LongArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderLongArray_infix")
@IgnorableReturnValue
infix fun LongArray.shouldNotContainInOrder(expected: LongArray): LongArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// FloatArray
@IgnorableReturnValue
fun FloatArray.shouldContainInOrder(vararg ts: Float): FloatArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderFloatArray_infix")
@IgnorableReturnValue
infix fun FloatArray.shouldContainInOrder(expected: FloatArray): FloatArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun FloatArray.shouldNotContainInOrder(vararg ts: Float): FloatArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderFloatArray_infix")
@IgnorableReturnValue
infix fun FloatArray.shouldNotContainInOrder(expected: FloatArray): FloatArray = apply { asList().shouldNotContainInOrder(expected.asList()) }

// DoubleArray
@IgnorableReturnValue
fun DoubleArray.shouldContainInOrder(vararg ts: Double): DoubleArray = apply { asList().shouldContainInOrder(ts.asList()) }
@JvmName("shouldContainInOrderDoubleArray_infix")
@IgnorableReturnValue
infix fun DoubleArray.shouldContainInOrder(expected: DoubleArray): DoubleArray = apply { asList().shouldContainInOrder(expected.asList()) }
@IgnorableReturnValue
fun DoubleArray.shouldNotContainInOrder(vararg ts: Double): DoubleArray = apply { asList().shouldNotContainInOrder(ts.asList()) }
@JvmName("shouldNotContainInOrderDoubleArray_infix")
@IgnorableReturnValue
infix fun DoubleArray.shouldNotContainInOrder(expected: DoubleArray): DoubleArray = apply { asList().shouldNotContainInOrder(expected.asList()) }
