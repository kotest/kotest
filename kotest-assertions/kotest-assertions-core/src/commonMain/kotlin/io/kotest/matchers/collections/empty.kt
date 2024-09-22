package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.invokeMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun BooleanArray?.shouldBeEmpty(): BooleanArray {
   if (this == null) fail("BooleanArray")
   asList() should beEmpty("BooleanArray")
   return this
}

fun BooleanArray?.shouldNotBeEmpty(): BooleanArray {
   if (this == null) fail("BooleanArray")
   asList() shouldNot beEmpty("BooleanArray")
   return this
}

fun ByteArray?.shouldBeEmpty(): ByteArray {
   if (this == null) fail("ByteArray")
   asList() should beEmpty("ByteArray")
   return this
}

fun ByteArray?.shouldNotBeEmpty(): ByteArray {
   if (this == null) fail("ByteArray")
   asList() shouldNot beEmpty("ByteArray")
   return this
}

fun ShortArray?.shouldBeEmpty(): ShortArray {
   if (this == null) fail("ShortArray")
   asList() should beEmpty("ShortArray")
   return this
}

fun ShortArray?.shouldNotBeEmpty(): ShortArray {
   if (this == null) fail("ShortArray")
   asList() shouldNot beEmpty("ShortArray")
   return this
}

fun CharArray?.shouldBeEmpty(): CharArray {
   if (this == null) fail("CharArray")
   asList() should beEmpty("CharArray")
   return this
}

fun CharArray?.shouldNotBeEmpty(): CharArray {
   if (this == null) fail("CharArray")
   asList() shouldNot beEmpty("CharArray")
   return this
}

fun IntArray?.shouldBeEmpty(): IntArray {
   if (this == null) fail("IntArray")
   asList() should beEmpty("IntArray")
   return this
}

fun IntArray?.shouldNotBeEmpty(): IntArray {
   if (this == null) fail("IntArray")
   asList() shouldNot beEmpty("IntArray")
   return this
}

fun LongArray?.shouldBeEmpty(): LongArray {
   if (this == null) fail("LongArray")
   asList() should beEmpty("LongArray")
   return this
}

fun LongArray?.shouldNotBeEmpty(): LongArray {
   if (this == null) fail("LongArray")
   asList() shouldNot beEmpty("LongArray")
   return this
}

fun FloatArray?.shouldBeEmpty(): FloatArray {
   if (this == null) fail("FloatArray")
   asList() should beEmpty("FloatArray")
   return this
}

fun FloatArray?.shouldNotBeEmpty(): FloatArray {
   if (this == null) fail("FloatArray")
   asList() shouldNot beEmpty("FloatArray")
   return this
}

fun <T> Array<T>?.shouldBeEmpty(): Array<T> {
   if (this == null) fail("Array")
   asList() should beEmpty("Array")
   return this
}

fun <T> Array<T>?.shouldNotBeEmpty(): Array<T> {
   if (this == null) fail("Array")
   asList() shouldNot beEmpty("Array")
   return this
}

fun <T, C : Collection<T>> C?.shouldBeEmpty(): C {
   if (this == null) fail("Collection")
   this should beEmpty("Collection")
   return this
}

fun <T, C : Collection<T>> C?.shouldNotBeEmpty(): C {
   if (this == null) fail("Collection")
   this shouldNot beEmpty("Collection")
   return this
}

fun <T, I : Iterable<T>> I?.shouldBeEmpty(): I {
   if (this == null) fail("Iterable")
   toList() should beEmpty(this.matcherName())
   return this
}

fun <T, I : Iterable<T>> I?.shouldNotBeEmpty(): I {
   if (this == null) fail("Iterable")
   toList() shouldNot beEmpty(this.matcherName())
   return this
}

private fun <T> beEmpty(name: String): Matcher<Collection<T>> = object : Matcher<Collection<T>> {
   override fun test(value: Collection<T>): MatcherResult = MatcherResult(
      value.isEmpty(),
      { "$name should be empty but contained ${value.first().print().value}" },
      { "$name should not be empty" }
   )
}

private inline fun fail(name: String): Nothing {
   invokeMatcher(null, Matcher.failure("Expected $name but was null"))
   throw NotImplementedError()
}

private inline fun Iterable<*>.matcherName() = when (this) {
   is ClosedRange<*>, is OpenEndRange<*> -> "Range"
   else -> "Iterable"
}

@Deprecated("Use Collection.shouldBeEmpty() or Collection.shouldNotBeEmpty()", level = DeprecationLevel.ERROR)
fun <T> beEmpty(): Matcher<Collection<T>> = beEmpty("Collection")

@Deprecated("Use Array.shouldBeEmpty() or Array.shouldNotBeEmpty()", level = DeprecationLevel.ERROR)
fun <T> beEmptyArray(): Matcher<Array<T>> = object : Matcher<Array<T>> {
   override fun test(value: Array<T>): MatcherResult = MatcherResult(
      value.isEmpty(),
      { "Array should be empty but contained ${value.first().print().value}" },
      { "Array should not be empty" }
   )
}
