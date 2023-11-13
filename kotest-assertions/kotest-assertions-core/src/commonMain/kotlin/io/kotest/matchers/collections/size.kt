package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldHave
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldHaveSize(size: Int): Iterable<T> {
   toList().shouldHaveSize(size)
   return this
}

infix fun <T> Array<T>.shouldHaveSize(size: Int): Array<T> {
   asList().shouldHaveSize(size)
   return this
}

infix fun BooleanArray.shouldHaveSize(size: Int): BooleanArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun ByteArray.shouldHaveSize(size: Int): ByteArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun CharArray.shouldHaveSize(size: Int): CharArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun ShortArray.shouldHaveSize(size: Int): ShortArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun IntArray.shouldHaveSize(size: Int): IntArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun LongArray.shouldHaveSize(size: Int): LongArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun FloatArray.shouldHaveSize(size: Int): FloatArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun DoubleArray.shouldHaveSize(size: Int): DoubleArray {
   toTypedArray().shouldHaveSize(size)
   return this
}

infix fun <T> Collection<T>.shouldHaveSize(size: Int): Collection<T> {
   this should haveSize(size = size)
   return this
}

infix fun <T> Iterable<T>.shouldNotHaveSize(size: Int): Iterable<T> {
   toList().shouldNotHaveSize(size)
   return this
}

infix fun <T> Array<T>.shouldNotHaveSize(size: Int): Array<T> {
   asList().shouldNotHaveSize(size)
   return this
}

infix fun BooleanArray.shouldNotHaveSize(size: Int): BooleanArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun ByteArray.shouldNotHaveSize(size: Int): ByteArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun CharArray.shouldNotHaveSize(size: Int): CharArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun ShortArray.shouldNotHaveSize(size: Int): ShortArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun IntArray.shouldNotHaveSize(size: Int): IntArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun LongArray.shouldNotHaveSize(size: Int): LongArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun FloatArray.shouldNotHaveSize(size: Int): FloatArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun DoubleArray.shouldNotHaveSize(size: Int): DoubleArray {
   toTypedArray().shouldNotHaveSize(size)
   return this
}

infix fun <T> Collection<T>.shouldNotHaveSize(size: Int): Collection<T> {
   this shouldNot haveSize(size)
   return this
}

infix fun <T, U> Iterable<T>.shouldBeSameSizeAs(other: Collection<U>): Iterable<T> {
   toList().shouldBeSameSizeAs(other)
   return this
}

infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Collection<U>): Array<T> {
   asList().shouldBeSameSizeAs(other)
   return this
}

infix fun <T, U> Iterable<T>.shouldBeSameSizeAs(other: Iterable<U>): Iterable<T> {
   toList().shouldBeSameSizeAs(other.toList())
   return this
}

infix fun <T, U> Array<T>.shouldBeSameSizeAs(other: Array<U>): Array<T> {
   asList().shouldBeSameSizeAs(other.asList())
   return this
}

infix fun BooleanArray.shouldBeSameSizeAs(other: BooleanArray): BooleanArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun ByteArray.shouldBeSameSizeAs(other: ByteArray): ByteArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun CharArray.shouldBeSameSizeAs(other: CharArray): CharArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun ShortArray.shouldBeSameSizeAs(other: ShortArray): ShortArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun IntArray.shouldBeSameSizeAs(other: IntArray): IntArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun LongArray.shouldBeSameSizeAs(other: LongArray): LongArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun FloatArray.shouldBeSameSizeAs(other: FloatArray): FloatArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun DoubleArray.shouldBeSameSizeAs(other: DoubleArray): DoubleArray {
   toTypedArray().shouldBeSameSizeAs(other.toTypedArray())
   return this
}

infix fun <T, U> Collection<T>.shouldBeSameSizeAs(other: Collection<U>): Collection<T> {
   this should beSameSizeAs(other)
   return this
}

fun <T, U> beSameSizeAs(other: Collection<U>) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size == other.size,
      { "Collection of size ${value.size} should be the same size as collection of size ${other.size}" },
      { "Collection of size ${value.size} should not be the same size as collection of size ${other.size}" })
}

infix fun <T> Iterable<T>.shouldHaveAtLeastSize(n: Int): Iterable<T> {
   toList().shouldHaveAtLeastSize(n)
   return this
}

infix fun <T> Array<T>.shouldHaveAtLeastSize(n: Int): Array<T> {
   asList().shouldHaveAtLeastSize(n)
   return this
}

infix fun BooleanArray.shouldHaveAtLeastSize(size: Int): BooleanArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun ByteArray.shouldHaveAtLeastSize(size: Int): ByteArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun CharArray.shouldHaveAtLeastSize(size: Int): CharArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun ShortArray.shouldHaveAtLeastSize(size: Int): ShortArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun IntArray.shouldHaveAtLeastSize(size: Int): IntArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun LongArray.shouldHaveAtLeastSize(size: Int): LongArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun FloatArray.shouldHaveAtLeastSize(size: Int): FloatArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun DoubleArray.shouldHaveAtLeastSize(size: Int): DoubleArray {
   toTypedArray().shouldHaveAtLeastSize(size)
   return this
}

infix fun <T> Collection<T>.shouldHaveAtLeastSize(n: Int): Collection<T> {
   this shouldHave atLeastSize(n)
   return this
}

fun <T> atLeastSize(n: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size >= n,
      { "Collection ${value.print().value} should contain at least $n elements" },
      {
         "Collection ${value.print().value} should contain less than $n elements"
      })
}

infix fun <T> Iterable<T>.shouldHaveAtMostSize(n: Int): Iterable<T> {
   toList().shouldHaveAtMostSize(n)
   return this
}

infix fun <T> Array<T>.shouldHaveAtMostSize(n: Int): Array<T> {
   asList().shouldHaveAtMostSize(n)
   return this
}

infix fun BooleanArray.shouldHaveAtMostSize(size: Int): BooleanArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun ByteArray.shouldHaveAtMostSize(size: Int): ByteArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun CharArray.shouldHaveAtMostSize(size: Int): CharArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun ShortArray.shouldHaveAtMostSize(size: Int): ShortArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun IntArray.shouldHaveAtMostSize(size: Int): IntArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun LongArray.shouldHaveAtMostSize(size: Int): LongArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun FloatArray.shouldHaveAtMostSize(size: Int): FloatArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun DoubleArray.shouldHaveAtMostSize(size: Int): DoubleArray {
   toTypedArray().shouldHaveAtMostSize(size)
   return this
}

infix fun <T> Collection<T>.shouldHaveAtMostSize(n: Int): Collection<T> {
   this shouldHave atMostSize(n)
   return this
}

fun <T> atMostSize(n: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) = MatcherResult(
      value.size <= n,
      { "Collection ${value.print().value} should contain at most $n elements" },
      {
         "Collection ${value.print().value} should contain more than $n elements"
      })
}


fun <T> haveSizeMatcher(size: Int) = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>) =
      MatcherResult(
         value.size == size,
         { "Collection should have size $size but has size ${value.size}. Values: ${value.print().value}" },
         { "Collection should not have size $size. Values: ${value.print().value}" }
      )
}
