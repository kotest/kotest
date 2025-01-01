package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that the given [BooleanArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun BooleanArray.shouldBeUnique(): BooleanArray = apply { asList() should beUniqueByEquals("BooleanArray") }

/**
 * Asserts that the given [BooleanArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun BooleanArray.shouldNotBeUnique(): BooleanArray = apply { asList() shouldNot beUniqueByEquals("BooleanArray") }

/**
 * Asserts that the given [ByteArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun ByteArray.shouldBeUnique(): ByteArray = apply { asList() should beUniqueByEquals("ByteArray") }

/**
 * Asserts that the given [ByteArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun ByteArray.shouldNotBeUnique(): ByteArray = apply { asList() shouldNot beUniqueByEquals("ByteArray") }

/**
 * Asserts that the given [ShortArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun ShortArray.shouldBeUnique(): ShortArray = apply { asList() should beUniqueByEquals("ShortArray") }

/**
 * Asserts that the given [ShortArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun ShortArray.shouldNotBeUnique(): ShortArray = apply { asList() shouldNot beUniqueByEquals("ShortArray") }

/**
 * Asserts that the given [CharArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun CharArray.shouldBeUnique(): CharArray = apply { asList() should beUniqueByEquals("CharArray") }

/**
 * Asserts that the given [CharArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun CharArray.shouldNotBeUnique(): CharArray = apply { asList() shouldNot beUniqueByEquals("CharArray") }

/**
 * Asserts that the given [IntArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun IntArray.shouldBeUnique(): IntArray = apply { asList() should beUniqueByEquals("IntArray") }

/**
 * Asserts that the given [IntArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun IntArray.shouldNotBeUnique(): IntArray = apply { asList() shouldNot beUniqueByEquals("IntArray") }

/**
 * Asserts that the given [LongArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun LongArray.shouldBeUnique(): LongArray = apply { asList() should beUniqueByEquals("LongArray") }

/**
 * Asserts that the given [LongArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun LongArray.shouldNotBeUnique(): LongArray = apply { asList() shouldNot beUniqueByEquals("LongArray") }

/**
 * Asserts that the given [FloatArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun FloatArray.shouldBeUnique(): FloatArray = apply { asList() should beUniqueByEquals("FloatArray") }

/**
 * Asserts that the given [FloatArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun FloatArray.shouldNotBeUnique(): FloatArray = apply { asList() shouldNot beUniqueByEquals("FloatArray") }

/**
 * Asserts that the given [DoubleArray] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun DoubleArray.shouldBeUnique(): DoubleArray = apply { asList() should beUniqueByEquals("DoubleArray") }

/**
 * Asserts that the given [DoubleArray] contains duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun DoubleArray.shouldNotBeUnique(): DoubleArray = apply { asList() shouldNot beUniqueByEquals("DoubleArray") }

/**
 * Asserts that the given [Array] contains no duplicate elements using the default equality
 * method of the elements themselves.
 *
 * This assertion checks for uniqueness of elements based on their `equals` implementation.
 *
 * Example:
 * ```
 * val array = arrayOf("apple", "banana", "cherry")
 * array.shouldBeUnique()   // Assertion passes
 *
 * val array = arrayOf("apple", "banana", "apple")
 * array.shouldBeUnique()   // Assertion fails
 * ```
 *
 * @return the input instance is returned for chaining.
 */
fun <T> Array<T>.shouldBeUnique(): Array<T> = apply { asList() should beUniqueByEquals("Array") }

/**
 * Asserts that the given [Array] contains no duplicate elements using the provided
 * [comparator] for equality.
 *
 * This assertion uses the specified [comparator] to compare elements for uniqueness.
 *
 * Example:
 * ```
 * val array = arrayOf("apple", "banana", "APPLE")
 * array.shouldBeUnique(compareBy { it.first() })   // Assertion passes
 *
 * val array = arrayOf("apple", "banana", "apple")
 * array.shouldBeUnique(String.CASE_INSENSITIVE_ORDER)   // Assertion fails
 * ```
 *
 * @param comparator the [Comparator] used to compare elements for equality.
 * @return the input instance is returned for chaining.
 */
fun <T> Array<T>.shouldBeUnique(comparator: Comparator<T>): Array<T> = apply { asList() should beUniqueByCompare("Array", comparator) }

/**
 * Asserts that the given [Array] contains duplicate elements using the default equality
 * method of the elements themselves.
 *
 * This assertion checks for duplicates without any custom comparison logic.
 *
 * Example:
 * ```
 * val array = arrayOf("apple", "banana", "apple")
 * array.shouldNotBeUnique()   // Assertion passes
 *
 * val array = arrayOf("apple", "banana", "cherry")
 * array.shouldNotBeUnique()   // Assertion fails
 * ```
 *
 * @return the input instance is returned for chaining.
 */
fun <T> Array<T>.shouldNotBeUnique(): Array<T> = apply { asList() shouldNot beUniqueByEquals("Array") }

/**
 * Asserts that the given [Array] contains duplicate elements using the provided
 * [comparator] for equality.
 *
 * This assertion uses the specified [comparator] to compare elements for duplicates.
 *
 * Example:
 * ```
 * val array = arrayOf("apple", "banana", "APPLE")
 * array.shouldNotBeUnique(compareBy { it.first() })   // Assertion passes
 *
 * val array = arrayOf("apple", "banana", "apple")
 * array.shouldNotBeUnique(String.CASE_INSENSITIVE_ORDER)   // Assertion fails
 * ```
 *
 * @param comparator the [Comparator] used to compare elements for equality.
 * @return the input instance is returned for chaining.
 */
fun <T> Array<T>.shouldNotBeUnique(comparator: Comparator<T>): Array<T> = apply { asList() shouldNot beUniqueByCompare("Array", comparator) }



/**
 * Asserts that the given [Iterable] contains no duplicate elements using the default equality
 * method of the elements themselves.
 *
 * This assertion checks for uniqueness of elements based on their `equals` implementation.
 *
 * Example:
 * ```
 * val list = listOf("apple", "banana", "cherry")
 * list.shouldBeUnique()   // Assertion passes
 *
 * val list = listOf("apple", "banana", "apple")
 * list.shouldBeUnique()   // Assertion fails
 * ```
 *
 * @return the input instance is returned for chaining, maintaining the input type.
 */
fun <T, I : Iterable<T>> I.shouldBeUnique(): I = apply { this should beUniqueByEquals(null) }


/**
 * Asserts that the given [Iterable] contains no duplicate elements using the given
 * [comparator] for equality.
 *
 * This assertion uses the provided [comparator] to compare elements for uniqueness.
 *
 * Example:
 * ```
 * val list = listOf("apple", "banana", "APPLE")
 * list.shouldBeUnique(compareBy { it.first() })   // Assertion passes
 *
 * val list = listOf("apple", "banana", "apple")
 * list.shouldBeUnique(String.CASE_INSENSITIVE_ORDER)   // Assertion fails
 * ```
 *
 * @param comparator the [Comparator] used to compare elements for equality.
 * @return the input instance is returned for chaining, maintaining the input type.
 */
fun <T, I : Iterable<T>> I.shouldBeUnique(comparator: Comparator<T>): I = apply { this should beUniqueByCompare(null, comparator) }

/**
 * Asserts that the given [Iterable] contains duplicate elements using the default equality
 * method of the elements themselves.
 *
 * This assertion checks for duplicates without any custom comparison logic.
 *
 * Example:
 * ```
 * val list = listOf("apple", "banana", "apple")
 * list.shouldNotBeUnique()   // Assertion passes
 *
 * val list = listOf("apple", "banana", "cherry")
 * list.shouldNotBeUnique()   // Assertion fails
 * ```
 *
 * @return the input instance is returned for chaining, maintaining the input type.
 */
fun <T, I : Iterable<T>> I.shouldNotBeUnique(): I = apply { this shouldNot beUniqueByEquals(null) }

/**
 * Asserts that the given [Iterable] contains duplicate elements using the provided
 * [comparator] for equality.
 *
 * This assertion uses the specified [comparator] to compare elements for duplicates.
 *
 * Example:
 * ```
 * val list = listOf("apple", "banana", "APPLE")
 * list.shouldNotBeUnique(compareBy { it.first() })   // Assertion passes
 *
 * val list = listOf("apple", "banana", "apple")
 * list.shouldNotBeUnique(String.CASE_INSENSITIVE_ORDER)   // Assertion fails
 * ```
 *
 * @param comparator the [Comparator] used to compare elements for equality.
 * @return the input instance is returned for chaining, maintaining the input type.
 */
fun <T, I : Iterable<T>> I.shouldNotBeUnique(comparator: Comparator<T>): I = apply { this shouldNot beUniqueByCompare(null, comparator) }

fun <T> beUnique(): Matcher<Iterable<T>> = beUniqueByEquals(null)

fun <T> beUnique(comparator: Comparator<T>): Matcher<Iterable<T>> = beUniqueByCompare(null, comparator)

internal fun <T> beUniqueByEquals(name: String?): Matcher<Iterable<T>> = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>): MatcherResult {
      val name = name ?: value.containerName()
      val report = value.duplicationByEqualsReport()
      return MatcherResult(
         !report.hasDuplicates(),
         { "$name should be unique, but has:\n${report.standardMessage()}" },
         { "$name should contain duplicates, but all elements are unique" }
      )
   }
}

internal fun <T> beUniqueByCompare(
   name: String?,
   comparator: Comparator<T>
): Matcher<Iterable<T>> = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>): MatcherResult {
      val name = name ?: value.containerName()
      val report = value.duplicationByCompareReportWith(comparator)
      return MatcherResult(
         !report.hasDuplicates(),
         { "$name should be unique by comparison, but has:\n${report.standardMessage()}" },
         { "$name should contain duplicates by comparison, but all elements are unique" }
      )
   }
}
