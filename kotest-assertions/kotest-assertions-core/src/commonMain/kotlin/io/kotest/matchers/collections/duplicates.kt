package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun BooleanArray.shouldContainDuplicates(): BooleanArray {
   asList() should containDuplicates("BooleanArray")
   return this
}

fun BooleanArray.shouldNotContainDuplicates(): BooleanArray {
   asList() shouldNot containDuplicates("BooleanArray")
   return this
}

fun ByteArray.shouldContainDuplicates(): ByteArray {
   asList() should containDuplicates("ByteArray")
   return this
}

fun ByteArray.shouldNotContainDuplicates(): ByteArray {
   asList() shouldNot containDuplicates("ByteArray")
   return this
}

fun ShortArray.shouldContainDuplicates(): ShortArray {
   asList() should containDuplicates("ShortArray")
   return this
}

fun ShortArray.shouldNotContainDuplicates(): ShortArray {
   asList() shouldNot containDuplicates("ShortArray")
   return this
}

fun CharArray.shouldContainDuplicates(): CharArray {
   asList() should containDuplicates("CharArray")
   return this
}

fun CharArray.shouldNotContainDuplicates(): CharArray {
   asList() shouldNot containDuplicates("CharArray")
   return this
}

fun IntArray.shouldContainDuplicates(): IntArray {
   asList() should containDuplicates("IntArray")
   return this
}

fun IntArray.shouldNotContainDuplicates(): IntArray {
   asList() shouldNot containDuplicates("IntArray")
   return this
}

fun LongArray.shouldContainDuplicates(): LongArray {
   asList() should containDuplicates("LongArray")
   return this
}

fun LongArray.shouldNotContainDuplicates(): LongArray {
   asList() shouldNot containDuplicates("LongArray")
   return this
}

fun FloatArray.shouldContainDuplicates(): FloatArray {
   asList() should containDuplicates("FloatArray")
   return this
}

fun FloatArray.shouldNotContainDuplicates(): FloatArray {
   asList() shouldNot containDuplicates("FloatArray")
   return this
}

fun DoubleArray.shouldContainDuplicates(): DoubleArray {
   asList() should containDuplicates("DoubleArray")
   return this
}

fun DoubleArray.shouldNotContainDuplicates(): DoubleArray {
   asList() shouldNot containDuplicates("DoubleArray")
   return this
}

fun <T> Array<T>.shouldContainDuplicates(): Array<T> {
   asList() should containDuplicates("Array")
   return this
}

fun <T> Array<T>.shouldNotContainDuplicates(): Array<T> {
   asList() shouldNot containDuplicates("Array")
   return this
}

fun <T, C : Collection<T>> C.shouldContainDuplicates(): C {
   this should containDuplicates(null)
   return this
}

fun <T, C : Collection<T>> C.shouldNotContainDuplicates(): C {
   this shouldNot containDuplicates(null)
   return this
}

fun <T, I : Iterable<T>> I.shouldContainDuplicates(): I {
   this should containDuplicates(null)
   return this
}

fun <T, I : Iterable<T>> I.shouldNotContainDuplicates(): I {
   this shouldNot containDuplicates(null)
   return this
}

fun <T> containDuplicates(): Matcher<Iterable<T>> = containDuplicates(null)

internal fun <T> containDuplicates(name: String?) = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>): MatcherResult {
      val name = name ?: value.containerName()
      val duplicates = value.duplicates()
      return MatcherResult(
         duplicates.isNotEmpty(),
         { "$name should contain duplicates" },
         { "$name should not contain duplicates, but has some: ${duplicates.print().value}" })
   }
}

internal fun <T> Iterable<T>.duplicates(): List<T> = this.groupingBy { it }
   .eachCount().entries
   .filter { it.value > 1 }
   .map { it.key }
