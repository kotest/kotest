package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// region Primitive Type Matchers

/**
 * Asserts that the size of this [ByteArray] is larger than the size of [other].
 *
 * Verifies that the current [ByteArray] has more elements than the specified [other] array.
 *
 * Opposite of [ByteArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * byteArrayOf(1, 2, 3) shouldBeLargerThan byteArrayOf(1, 2)    // Assertion passes
 * byteArrayOf(1) shouldBeLargerThan byteArrayOf(1, 2, 3)      // Assertion fails
 * ```
 *
 * @see ByteArray.shouldNotBeLargerThan
 */
infix fun ByteArray.shouldBeLargerThan(other: ByteArray): ByteArray = apply {
   asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [ByteArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [ByteArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [ByteArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * byteArrayOf(1) shouldNotBeLargerThan byteArrayOf(1, 2, 3)    // Assertion passes
 * byteArrayOf(1, 2, 3) shouldNotBeLargerThan byteArrayOf(1, 2) // Assertion fails
 * ```
 *
 * @see ByteArray.shouldBeLargerThan
 */
infix fun ByteArray.shouldNotBeLargerThan(other: ByteArray): ByteArray = apply {
   asList() shouldNotBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [IntArray] is larger than the size of [other].
 *
 * Verifies that the current [IntArray] has more elements than the specified [other] array.
 *
 * Opposite of [IntArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * intArrayOf(1, 2, 3) shouldBeLargerThan intArrayOf(1, 2)    // Assertion passes
 * intArrayOf(1) shouldBeLargerThan intArrayOf(1, 2, 3)      // Assertion fails
 * ```
 *
 * @see IntArray.shouldNotBeLargerThan
 */
infix fun IntArray.shouldBeLargerThan(other: IntArray): IntArray = apply {
   asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [IntArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [IntArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [IntArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * intArrayOf(1) shouldNotBeLargerThan intArrayOf(1, 2, 3)    // Assertion passes
 * intArrayOf(1, 2, 3) shouldNotBeLargerThan intArrayOf(1, 2) // Assertion fails
 * ```
 *
 * @see IntArray.shouldBeLargerThan
 */
infix fun IntArray.shouldNotBeLargerThan(other: IntArray): IntArray = apply {
   asList() shouldNotBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [ShortArray] is larger than the size of [other].
 *
 * Verifies that the current [ShortArray] has more elements than the specified [other] array.
 *
 * Opposite of [ShortArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * shortArrayOf(1, 2, 3) shouldBeLargerThan shortArrayOf(1, 2)    // Assertion passes
 * shortArrayOf(1) shouldBeLargerThan shortArrayOf(1, 2, 3)      // Assertion fails
 * ```
 *
 * @see ShortArray.shouldNotBeLargerThan
 */
infix fun ShortArray.shouldBeLargerThan(other: ShortArray): ShortArray = apply {
   asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [ShortArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [ShortArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [ShortArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * shortArrayOf(1) shouldNotBeLargerThan shortArrayOf(1, 2, 3)    // Assertion passes
 * shortArrayOf(1, 2, 3) shouldNotBeLargerThan shortArrayOf(1, 2) // Assertion fails
 * ```
 *
 * @see ShortArray.shouldBeLargerThan
 */
infix fun ShortArray.shouldNotBeLargerThan(other: ShortArray): ShortArray = apply {
   asList() shouldNotBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [LongArray] is larger than the size of [other].
 *
 * Verifies that the current [LongArray] has more elements than the specified [other] array.
 *
 * Opposite of [LongArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * longArrayOf(1, 2, 3) shouldBeLargerThan longArrayOf(1, 2)    // Assertion passes
 * longArrayOf(1) shouldBeLargerThan longArrayOf(1, 2, 3)      // Assertion fails
 * ```
 *
 * @see LongArray.shouldNotBeLargerThan
 */
infix fun LongArray.shouldBeLargerThan(other: LongArray): LongArray = apply {
   asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [LongArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [LongArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [LongArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * longArrayOf(1) shouldNotBeLargerThan longArrayOf(1, 2, 3)    // Assertion passes
 * longArrayOf(1, 2, 3) shouldNotBeLargerThan longArrayOf(1, 2) // Assertion fails
 * ```
 *
 * @see LongArray.shouldBeLargerThan
 */
infix fun LongArray.shouldNotBeLargerThan(other: LongArray): LongArray = apply {
   asList() shouldNotBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [FloatArray] is larger than the size of [other].
 *
 * Verifies that the current [FloatArray] has more elements than the specified [other] array.
 *
 * Opposite of [FloatArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * floatArrayOf(1.1f, 2.2f, 3.3f) shouldBeLargerThan floatArrayOf(1.1f, 2.2f)    // Assertion passes
 * floatArrayOf(1.1f) shouldBeLargerThan floatArrayOf(1.1f, 2.2f, 3.3f)        // Assertion fails
 * ```
 *
 * @see FloatArray.shouldNotBeLargerThan
 */
infix fun FloatArray.shouldBeLargerThan(other: FloatArray): FloatArray = apply {
   this.asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [FloatArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [FloatArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [FloatArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * floatArrayOf(1.1f) shouldNotBeLargerThan floatArrayOf(1.1f, 2.2f, 3.3f)    // Assertion passes
 * floatArrayOf(1.1f, 2.2f, 3.3f) shouldNotBeLargerThan floatArrayOf(1.1f, 2.2f) // Assertion fails
 * ```
 *
 * @see FloatArray.shouldBeLargerThan
 */
infix fun FloatArray.shouldNotBeLargerThan(other: FloatArray): FloatArray = apply {
   this.asList() shouldNotBeLargerThan other.asList()
}


/**
 * Asserts that the size of this [DoubleArray] is larger than the size of [other].
 *
 * Verifies that the current [DoubleArray] has more elements than the specified [other] array.
 *
 * Opposite of [DoubleArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * doubleArrayOf(1.1, 2.2, 3.3) shouldBeLargerThan doubleArrayOf(1.1, 2.2)    // Assertion passes
 * doubleArrayOf(1.1) shouldBeLargerThan doubleArrayOf(1.1, 2.2, 3.3)        // Assertion fails
 * ```
 *
 * @see DoubleArray.shouldNotBeLargerThan
 */
infix fun DoubleArray.shouldBeLargerThan(other: DoubleArray): DoubleArray = apply {
   this.asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [DoubleArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [DoubleArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [DoubleArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * doubleArrayOf(1.1) shouldNotBeLargerThan doubleArrayOf(1.1, 2.2, 3.3)    // Assertion passes
 * doubleArrayOf(1.1, 2.2, 3.3) shouldNotBeLargerThan doubleArrayOf(1.1, 2.2) // Assertion fails
 * ```
 *
 * @see DoubleArray.shouldBeLargerThan
 */
infix fun DoubleArray.shouldNotBeLargerThan(other: DoubleArray): DoubleArray = apply {
   this.asList() shouldNotBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [CharArray] is larger than the size of [other].
 *
 * Verifies that the current [CharArray] has more elements than the specified [other] array.
 *
 * Opposite of [CharArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * charArrayOf('a', 'b', 'c') shouldBeLargerThan charArrayOf('a', 'b')    // Assertion passes
 * charArrayOf('a') shouldBeLargerThan charArrayOf('a', 'b', 'c')        // Assertion fails
 * ```
 *
 * @see CharArray.shouldNotBeLargerThan
 */
infix fun CharArray.shouldBeLargerThan(other: CharArray): CharArray = apply {
   this.asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [CharArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [CharArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [CharArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * charArrayOf('a') shouldNotBeLargerThan charArrayOf('a', 'b', 'c')    // Assertion passes
 * charArrayOf('a', 'b', 'c') shouldNotBeLargerThan charArrayOf('a', 'b') // Assertion fails
 * ```
 *
 * @see CharArray.shouldBeLargerThan
 */
infix fun CharArray.shouldNotBeLargerThan(other: CharArray): CharArray = apply {
   this.asList() shouldNotBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [BooleanArray] is larger than the size of [other].
 *
 * Verifies that the current [BooleanArray] has more elements than the specified [other] array.
 *
 * Opposite of [BooleanArray.shouldNotBeLargerThan].
 *
 * Example:
 * ```
 * booleanArrayOf(true, false, true) shouldBeLargerThan booleanArrayOf(true, false)    // Assertion passes
 * booleanArrayOf(true) shouldBeLargerThan booleanArrayOf(true, false, true)          // Assertion fails
 * ```
 *
 * @see BooleanArray.shouldNotBeLargerThan
 */
infix fun BooleanArray.shouldBeLargerThan(other: BooleanArray): BooleanArray = apply {
   this.asList() shouldBeLargerThan other.asList()
}

/**
 * Asserts that the size of this [BooleanArray] is NOT larger than the size of [other].
 *
 * Verifies that the current [BooleanArray] does not have more elements than the specified [other] array.
 *
 * Opposite of [BooleanArray.shouldBeLargerThan].
 *
 * Example:
 * ```
 * booleanArrayOf(true) shouldNotBeLargerThan booleanArrayOf(true, false, true)    // Assertion passes
 * booleanArrayOf(true, false, true) shouldNotBeLargerThan booleanArrayOf(true, false) // Assertion fails
 * ```
 *
 * @see BooleanArray.shouldBeLargerThan
 */
infix fun BooleanArray.shouldNotBeLargerThan(other: BooleanArray): BooleanArray = apply {
   this.asList() shouldNotBeLargerThan other.asList()
}

// endregion

/**
 * Asserts that this [Iterable] is larger than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection contains more elements than the
 * specified [other] collection.
 *
 * Opposite of [Iterable.shouldNotBeLargerThan].
 *
 * ```
 * listOf(1, 2, 3) shouldBeLargerThan listOf(1, 2)    // Assertion passes
 * listOf(1) shouldBeLargerThan listOf(1, 2, 3)       // Assertion fails
 * ```
 *
 * @see Iterable.shouldNotBeLargerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldBeLargerThan(other: Iterable<U>): I = apply {
   toList() should beLargerThan(other)
}

/**
 * Asserts that this [Iterable] is NOT larger than [other].
 *
 * Compares the sizes of two collections and verifies that the current collection does not contain more elements than
 * the specified [other] collection.
 *
 * Opposite of [Iterable.shouldBeLargerThan].
 *
 * ```
 * listOf(1) shouldNotBeLargerThan listOf(1, 2, 3)       // Assertion passes
 * listOf(1, 2, 3) shouldNotBeLargerThan listOf(1, 2)    // Assertion fails
 * ```
 *
 * @see Iterable.shouldBeLargerThan
 */
infix fun <T, U, I : Iterable<T>> I.shouldNotBeLargerThan(other: Iterable<U>): I = apply {
   toList() shouldNot beLargerThan(other)
}

/**
 * Asserts that this [Array] is larger than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array contains more elements than the specified
 * [other] array.
 *
 * Opposite of [Array.shouldNotBeLargerThan].
 *
 * ```
 * arrayOf(1, 2, 3) shouldBeLargerThan arrayOf(1, 2)    // Assertion passes
 * arrayOf(1) shouldBeLargerThan arrayOf(1, 2, 3)       // Assertion fails
 * ```
 *
 * @see Array.shouldNotBeLargerThan
 */
infix fun <T, U> Array<T>.shouldBeLargerThan(other: Array<U>): Array<T> = apply {
   asList().shouldBeLargerThan(other.asList())
}

/**
 * Asserts that this [Array] is NOT larger than [other].
 *
 * Compares the sizes of two arrays and verifies that the current array does not contain more elements than the
 * specified [other] array.
 *
 * Opposite of [Array.shouldBeLargerThan].
 *
 * ```
 * arrayOf(1) shouldNotBeLargerThan arrayOf(1, 2, 3)         // Assertion passes
 * arrayOf(1, 2, 3) shouldNotBeLargerThan arrayOf(1, 2)      // Assertion fails
 * ```
 *
 * @see Array.shouldBeLargerThan
 */
infix fun <T, U> Array<T>.shouldNotBeLargerThan(other: Array<U>): Array<T> = apply {
   asList().shouldNotBeLargerThan(other.asList())
}

/**
 * Matcher that asserts the size of an [Iterable] is larger than the size of [other].
 *
 * This matcher returns a successful result if the number of elements in the tested [Iterable]
 * is strictly greater than the number of elements in [other].
 *
 * Example:
 * ```
 * listOf(1, 2, 3) should beLargerThan(listOf(1, 2))    // Assertion passes
 * listOf(1) shouldNot beLargerThan(listOf(1, 2, 3))    // Assertion passes
 * ```
 *
 * @param other the [Iterable] to compare against.
 * @return a [Matcher] that validates the size relationship.
 * @see Iterable.shouldBeLargerThan
 * @see Iterable.shouldNotBeLargerThan
 */
fun <T, U> beLargerThan(other: Iterable<U>) = object : Matcher<Iterable<T>> {
   val otherSize = other.count()
   override fun test(value: Iterable<T>) = MatcherResult(
      value.count() > otherSize,
      { "Collection of size ${value.count()} should be larger than collection of size $otherSize" },
      { "Collection of size ${value.count()} should not be larger than collection of size $otherSize" }
   )
}
