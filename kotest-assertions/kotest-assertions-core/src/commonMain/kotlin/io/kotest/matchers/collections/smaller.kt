package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// region Primitive Type Matchers

/**
 * Asserts that this [ByteArray] is smaller than [other].
 *
 * Compares the sizes of two [ByteArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [ByteArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * byteArrayOf(1, 2) shouldBeSmallerThan byteArrayOf(1, 2, 3)    // Assertion passes
 * byteArrayOf(1, 2, 3) shouldBeSmallerThan byteArrayOf(1, 2)    // Assertion fails
 * ```
 *
 * @see ByteArray.shouldNotBeSmallerThan
 */
infix fun ByteArray.shouldBeSmallerThan(other: ByteArray): ByteArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [ByteArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [ByteArray]s and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [ByteArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * byteArrayOf(1, 2, 3) shouldNotBeSmallerThan byteArrayOf(1, 2)    // Assertion passes
 * byteArrayOf(1, 2) shouldNotBeSmallerThan byteArrayOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see ByteArray.shouldBeSmallerThan
 */
infix fun ByteArray.shouldNotBeSmallerThan(other: ByteArray): ByteArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

// IntArray Matchers

/**
 * Asserts that this [IntArray] is smaller than [other].
 *
 * Compares the sizes of two [IntArray]s and verifies that the current array contains fewer elements than the specified
 * [other] array.
 *
 * Opposite of [IntArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * intArrayOf(1, 2) shouldBeSmallerThan intArrayOf(1, 2, 3)    // Assertion passes
 * intArrayOf(1, 2, 3) shouldBeSmallerThan intArrayOf(1, 2)    // Assertion fails
 * ```
 *
 * @see IntArray.shouldNotBeSmallerThan
 */
infix fun IntArray.shouldBeSmallerThan(other: IntArray): IntArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [IntArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [IntArray]s and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [IntArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * intArrayOf(1, 2, 3) shouldNotBeSmallerThan intArrayOf(1, 2)    // Assertion passes
 * intArrayOf(1, 2) shouldNotBeSmallerThan intArrayOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see IntArray.shouldBeSmallerThan
 */
infix fun IntArray.shouldNotBeSmallerThan(other: IntArray): IntArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

// ShortArray Matchers

/**
 * Asserts that this [ShortArray] is smaller than [other].
 *
 * Compares the sizes of two [ShortArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [ShortArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * shortArrayOf(1, 2) shouldBeSmallerThan shortArrayOf(1, 2, 3)    // Assertion passes
 * shortArrayOf(1, 2, 3) shouldBeSmallerThan shortArrayOf(1, 2)    // Assertion fails
 * ```
 *
 * @see ShortArray.shouldNotBeSmallerThan
 */
infix fun ShortArray.shouldBeSmallerThan(other: ShortArray): ShortArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [ShortArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [ShortArray]s and verifies that the current array contains at least as many elements as
 * the specified [other] array.
 *
 * Opposite of [ShortArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * shortArrayOf(1, 2, 3) shouldNotBeSmallerThan shortArrayOf(1, 2)    // Assertion passes
 * shortArrayOf(1, 2) shouldNotBeSmallerThan shortArrayOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see ShortArray.shouldBeSmallerThan
 */
infix fun ShortArray.shouldNotBeSmallerThan(other: ShortArray): ShortArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

/**
 * Asserts that this [LongArray] is smaller than [other].
 *
 * Compares the sizes of two [LongArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [LongArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * longArrayOf(1L, 2L) shouldBeSmallerThan longArrayOf(1L, 2L, 3L)    // Assertion passes
 * longArrayOf(1L, 2L, 3L) shouldBeSmallerThan longArrayOf(1L, 2L)    // Assertion fails
 * ```
 *
 * @see LongArray.shouldNotBeSmallerThan
 */
infix fun LongArray.shouldBeSmallerThan(other: LongArray): LongArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [LongArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [LongArray]s and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [LongArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * longArrayOf(1L, 2L, 3L) shouldNotBeSmallerThan longArrayOf(1L, 2L)    // Assertion passes
 * longArrayOf(1L, 2L) shouldNotBeSmallerThan longArrayOf(1L, 2L, 3L)    // Assertion fails
 * ```
 *
 * @see LongArray.shouldBeSmallerThan
 */
infix fun LongArray.shouldNotBeSmallerThan(other: LongArray): LongArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

/**
 * Asserts that this [FloatArray] is smaller than [other].
 *
 * Compares the sizes of two [FloatArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [FloatArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * floatArrayOf(1.1f, 2.2f) shouldBeSmallerThan floatArrayOf(1.1f, 2.2f, 3.3f)    // Assertion passes
 * floatArrayOf(1.1f, 2.2f, 3.3f) shouldBeSmallerThan floatArrayOf(1.1f, 2.2f)    // Assertion fails
 * ```
 *
 * @see FloatArray.shouldNotBeSmallerThan
 */
infix fun FloatArray.shouldBeSmallerThan(other: FloatArray): FloatArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [FloatArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [FloatArray]s and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [FloatArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * floatArrayOf(1.1f, 2.2f, 3.3f) shouldNotBeSmallerThan floatArrayOf(1.1f, 2.2f)    // Assertion passes
 * floatArrayOf(1.1f, 2.2f) shouldNotBeSmallerThan floatArrayOf(1.1f, 2.2f, 3.3f)    // Assertion fails
 * ```
 *
 * @see FloatArray.shouldBeSmallerThan
 */
infix fun FloatArray.shouldNotBeSmallerThan(other: FloatArray): FloatArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

/**
 * Asserts that this [DoubleArray] is smaller than [other].
 *
 * Compares the sizes of two [DoubleArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [DoubleArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * doubleArrayOf(1.1, 2.2) shouldBeSmallerThan doubleArrayOf(1.1, 2.2, 3.3)    // Assertion passes
 * doubleArrayOf(1.1, 2.2, 3.3) shouldBeSmallerThan doubleArrayOf(1.1, 2.2)    // Assertion fails
 * ```
 *
 * @see DoubleArray.shouldNotBeSmallerThan
 */
infix fun DoubleArray.shouldBeSmallerThan(other: DoubleArray): DoubleArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [DoubleArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [DoubleArray]s and verifies that the current array contains at least as many elements as
 * the specified [other] array.
 *
 * Opposite of [DoubleArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * doubleArrayOf(1.1, 2.2, 3.3) shouldNotBeSmallerThan doubleArrayOf(1.1, 2.2)    // Assertion passes
 * doubleArrayOf(1.1, 2.2) shouldNotBeSmallerThan doubleArrayOf(1.1, 2.2, 3.3)    // Assertion fails
 * ```
 *
 * @see DoubleArray.shouldBeSmallerThan
 */
infix fun DoubleArray.shouldNotBeSmallerThan(other: DoubleArray): DoubleArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

/**
 * Asserts that this [CharArray] is smaller than [other].
 *
 * Compares the sizes of two [CharArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [CharArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * charArrayOf('a', 'b') shouldBeSmallerThan charArrayOf('a', 'b', 'c')    // Assertion passes
 * charArrayOf('a', 'b', 'c') shouldBeSmallerThan charArrayOf('a', 'b')    // Assertion fails
 * ```
 *
 * @see CharArray.shouldNotBeSmallerThan
 */
infix fun CharArray.shouldBeSmallerThan(other: CharArray): CharArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [CharArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [CharArray]s and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [CharArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * charArrayOf('a', 'b', 'c') shouldNotBeSmallerThan charArrayOf('a', 'b')    // Assertion passes
 * charArrayOf('a', 'b') shouldNotBeSmallerThan charArrayOf('a', 'b', 'c')    // Assertion fails
 * ```
 *
 * @see CharArray.shouldBeSmallerThan
 */
infix fun CharArray.shouldNotBeSmallerThan(other: CharArray): CharArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

/**
 * Asserts that this [BooleanArray] is smaller than [other].
 *
 * Compares the sizes of two [BooleanArray]s and verifies that the current array contains fewer elements than the
 * specified [other] array.
 *
 * Opposite of [BooleanArray.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * booleanArrayOf(true, false) shouldBeSmallerThan booleanArrayOf(true, false, true)    // Assertion passes
 * booleanArrayOf(true, false, true) shouldBeSmallerThan booleanArrayOf(true, false)    // Assertion fails
 * ```
 *
 * @see BooleanArray.shouldNotBeSmallerThan
 */
infix fun BooleanArray.shouldBeSmallerThan(other: BooleanArray): BooleanArray = apply {
   this.asList() shouldBeSmallerThan other.asList()
}

/**
 * Asserts that this [BooleanArray] is NOT smaller than [other].
 *
 * Compares the sizes of two [BooleanArray]s and verifies that the current array contains at least as many elements as
 * the specified [other] array.
 *
 * Opposite of [BooleanArray.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * booleanArrayOf(true, false, true) shouldNotBeSmallerThan booleanArrayOf(true, false)    // Assertion passes
 * booleanArrayOf(true, false) shouldNotBeSmallerThan booleanArrayOf(true, false, true)    // Assertion fails
 * ```
 *
 * @see BooleanArray.shouldBeSmallerThan
 */
infix fun BooleanArray.shouldNotBeSmallerThan(other: BooleanArray): BooleanArray = apply {
   this.asList() shouldNotBeSmallerThan other.asList()
}

// endregion


/**
 * Asserts that this [Iterable] is smaller than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection contains fewer elements than the
 * specified [other] collection.
 *
 * Opposite of [Iterable.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * listOf(1, 2) shouldBeSmallerThan listOf(1, 2, 3)    // Assertion passes
 * listOf(1, 2, 3) shouldBeSmallerThan listOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldNotBeSmallerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldBeSmallerThan(other: Iterable<U>): I = apply {
   toList() should beSmallerThan(other)
}

/**
 * Asserts that this [Array] is smaller than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array contains fewer elements than the specified
 * [other] array.
 *
 * Opposite of [Array.shouldNotBeSmallerThan].
 *
 * Example:
 * ```
 * arrayOf(1, 2) shouldBeSmallerThan arrayOf(1, 2, 3)    // Assertion passes
 * arrayOf(1, 2, 3) shouldBeSmallerThan arrayOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Array.shouldNotBeSmallerThan
 */
infix fun <T, U> Array<T>.shouldBeSmallerThan(other: Array<U>): Array<T> = apply {
   asList() should beSmallerThan(other.asList())
}


/**
 * Asserts that this [Iterable] is NOT smaller than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection contains at least as many elements as
 * the specified [other] collection.
 *
 * Opposite of [Iterable.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * listOf(1, 2, 3) shouldNotBeSmallerThan listOf(1, 2)    // Assertion passes
 * listOf(1, 2) shouldNotBeSmallerThan listOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldBeSmallerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldNotBeSmallerThan(other: Iterable<U>): I = apply {
   toList() shouldNot beSmallerThan(other)
}

/**
 * Asserts that this [Array] is NOT smaller than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array contains at least as many elements as the
 * specified [other] array.
 *
 * Opposite of [Array.shouldBeSmallerThan].
 *
 * Example:
 * ```
 * arrayOf(1, 2, 3) shouldNotBeSmallerThan arrayOf(1, 2)    // Assertion passes
 * arrayOf(1, 2) shouldNotBeSmallerThan arrayOf(1, 2, 3)    // Assertion fails
 * ```
 *
 * @see Array.shouldBeSmallerThan
 */
infix fun <T, U> Array<T>.shouldNotBeSmallerThan(other: Array<U>): Array<T> = apply {
   asList() shouldNot beSmallerThan(other.asList())
}

/**
 * Matcher that verifies a collection is smaller than [other].
 *
 * Compares the sizes of two collections and returns a positive result if the first collection contains fewer elements
 * than the second.
 *
 * Example:
 * ```
 * listOf(1, 2) should beSmallerThan listOf(1, 2, 3)    // Assertion passes
 * listOf(1, 2, 3) should beSmallerThan listOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldBeSmallerThan
 * @see Iterable.shouldNotBeSmallerThan
 */
fun <T, U> beSmallerThan(other: Iterable<U>) = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>) = MatcherResult(
      value.count() < other.count(),
      { "Collection of size ${value.count()} should be smaller than collection of size ${other.count()}" },
      { "Collection of size ${value.count()} should not be smaller than collection of size ${other.count()}" }
   )
}
