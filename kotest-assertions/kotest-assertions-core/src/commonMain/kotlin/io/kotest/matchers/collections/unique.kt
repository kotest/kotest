package io.kotest.matchers.collections

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun BooleanArray.shouldBeUnique(): BooleanArray {
   asList() should beUniqueByEquals("BooleanArray")
   return this
}

fun BooleanArray.shouldNotBeUnique(): BooleanArray {
   asList() shouldNot beUniqueByEquals("BooleanArray")
   return this
}

fun ByteArray.shouldBeUnique(): ByteArray {
   asList() should beUniqueByEquals("ByteArray")
   return this
}

fun ByteArray.shouldNotBeUnique(): ByteArray {
   asList() shouldNot beUniqueByEquals("ByteArray")
   return this
}

fun ShortArray.shouldBeUnique(): ShortArray {
   asList() should beUniqueByEquals("ShortArray")
   return this
}

fun ShortArray.shouldNotBeUnique(): ShortArray {
   asList() shouldNot beUniqueByEquals("ShortArray")
   return this
}

fun CharArray.shouldBeUnique(): CharArray {
   asList() should beUniqueByEquals("CharArray")
   return this
}

fun CharArray.shouldNotBeUnique(): CharArray {
   asList() shouldNot beUniqueByEquals("CharArray")
   return this
}

fun IntArray.shouldBeUnique(): IntArray {
   asList() should beUniqueByEquals("IntArray")
   return this
}

fun IntArray.shouldNotBeUnique(): IntArray {
   asList() shouldNot beUniqueByEquals("IntArray")
   return this
}

fun LongArray.shouldBeUnique(): LongArray {
   asList() should beUniqueByEquals("LongArray")
   return this
}

fun LongArray.shouldNotBeUnique(): LongArray {
   asList() shouldNot beUniqueByEquals("LongArray")
   return this
}

fun FloatArray.shouldBeUnique(): FloatArray {
   asList() should beUniqueByEquals("FloatArray")
   return this
}

fun FloatArray.shouldNotBeUnique(): FloatArray {
   asList() shouldNot beUniqueByEquals("FloatArray")
   return this
}

fun DoubleArray.shouldBeUnique(): DoubleArray {
   asList() should beUniqueByEquals("DoubleArray")
   return this
}

fun DoubleArray.shouldNotBeUnique(): DoubleArray {
   asList() shouldNot beUniqueByEquals("DoubleArray")
   return this
}

/**
 * Asserts that the given [Array] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining
 */
fun <T> Array<T>.shouldBeUnique(): Array<T> {
   asList() should beUniqueByEquals("Array")
   return this
}

/**
 * Asserts that the given [Array] contains no duplicate elements using the given
 * [comparator] for equality.
 *
 * @return the input instance is returned for chaining
 */
fun <T> Array<T>.shouldBeUnique(comparator: Comparator<T>): Array<T> {
   asList() should beUniqueByCompare("Array", comparator)
   return this
}

fun <T> Array<T>.shouldNotBeUnique(): Array<T> {
   asList() shouldNot beUniqueByEquals("Array")
   return this
}

fun <T, C : Collection<T>> C.shouldNotBeUnique(): C {
   this shouldNot beUniqueByEquals(null)
   return this
}

/**
 * Asserts that the given [Iterable] contains no duplicate elements using the equality
 * method of the elements themselves.
 *
 * @return the input instance is returned for chaining, maintaining the input type
 */
fun <T, I : Iterable<T>> I.shouldBeUnique(): I {
   this should beUniqueByEquals(null)
   return this
}

/**
 * Asserts that the given [Iterable] contains no duplicate elements using the given
 * [comparator] for equality.
 *
 * @return the input instance is returned for chaining, maintaining the input type
 */
fun <T, I : Iterable<T>> I.shouldBeUnique(comparator: Comparator<T>): I {
   this should beUniqueByCompare(null, comparator)
   return this
}

fun <T, I : Iterable<T>> I.shouldNotBeUnique(): I {
   this shouldNot beUniqueByEquals(null)
   return this
}

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
         { "$name should contain duplicates elements by comparison, but all elements are unique" }
      )
   }
}
