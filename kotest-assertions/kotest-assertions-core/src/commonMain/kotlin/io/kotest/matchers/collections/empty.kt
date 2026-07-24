package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.invokeMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

@IgnorableReturnValue
fun BooleanArray?.shouldBeEmpty(): BooleanArray {
   if (this == null) fail("BooleanArray")
   asList() should beEmpty("BooleanArray")
   return this
}

@IgnorableReturnValue
fun BooleanArray?.shouldNotBeEmpty(): BooleanArray {
   if (this == null) fail("BooleanArray")
   asList() shouldNot beEmpty("BooleanArray")
   return this
}

@IgnorableReturnValue
fun ByteArray?.shouldBeEmpty(): ByteArray {
   if (this == null) fail("ByteArray")
   asList() should beEmpty("ByteArray")
   return this
}

@IgnorableReturnValue
fun ByteArray?.shouldNotBeEmpty(): ByteArray {
   if (this == null) fail("ByteArray")
   asList() shouldNot beEmpty("ByteArray")
   return this
}

@IgnorableReturnValue
fun ShortArray?.shouldBeEmpty(): ShortArray {
   if (this == null) fail("ShortArray")
   asList() should beEmpty("ShortArray")
   return this
}

@IgnorableReturnValue
fun ShortArray?.shouldNotBeEmpty(): ShortArray {
   if (this == null) fail("ShortArray")
   asList() shouldNot beEmpty("ShortArray")
   return this
}

@IgnorableReturnValue
fun CharArray?.shouldBeEmpty(): CharArray {
   if (this == null) fail("CharArray")
   asList() should beEmpty("CharArray")
   return this
}

@IgnorableReturnValue
fun CharArray?.shouldNotBeEmpty(): CharArray {
   if (this == null) fail("CharArray")
   asList() shouldNot beEmpty("CharArray")
   return this
}

@IgnorableReturnValue
fun IntArray?.shouldBeEmpty(): IntArray {
   if (this == null) fail("IntArray")
   asList() should beEmpty("IntArray")
   return this
}

@IgnorableReturnValue
fun IntArray?.shouldNotBeEmpty(): IntArray {
   if (this == null) fail("IntArray")
   asList() shouldNot beEmpty("IntArray")
   return this
}

@IgnorableReturnValue
fun LongArray?.shouldBeEmpty(): LongArray {
   if (this == null) fail("LongArray")
   asList() should beEmpty("LongArray")
   return this
}

@IgnorableReturnValue
fun LongArray?.shouldNotBeEmpty(): LongArray {
   if (this == null) fail("LongArray")
   asList() shouldNot beEmpty("LongArray")
   return this
}

@IgnorableReturnValue
fun FloatArray?.shouldBeEmpty(): FloatArray {
   if (this == null) fail("FloatArray")
   asList() should beEmpty("FloatArray")
   return this
}

@IgnorableReturnValue
fun FloatArray?.shouldNotBeEmpty(): FloatArray {
   if (this == null) fail("FloatArray")
   asList() shouldNot beEmpty("FloatArray")
   return this
}

@IgnorableReturnValue
fun DoubleArray?.shouldBeEmpty(): DoubleArray {
   if (this == null) fail("DoubleArray")
   asList() should beEmpty("DoubleArray")
   return this
}

@IgnorableReturnValue
fun DoubleArray?.shouldNotBeEmpty(): DoubleArray {
   if (this == null) fail("DoubleArray")
   asList() shouldNot beEmpty("DoubleArray")
   return this
}

@IgnorableReturnValue
fun <T> Array<T>?.shouldBeEmpty(): Array<T> {
   if (this == null) fail("Array")
   asList() should beEmpty("Array")
   return this
}

@IgnorableReturnValue
fun <T> Array<T>?.shouldNotBeEmpty(): Array<T> {
   if (this == null) fail("Array")
   asList() shouldNot beEmpty("Array")
   return this
}

@IgnorableReturnValue
fun <T, I : Iterable<T>> I?.shouldBeEmpty(): I {
   if (this == null) fail("Iterable")
   this should beEmpty(null)
   return this
}

@IgnorableReturnValue
fun <T, I : Iterable<T>> I?.shouldNotBeEmpty(): I {
   if (this == null) fail("Iterable")
   this shouldNot beEmpty(null)
   return this
}

fun <T> beEmpty(): Matcher<Iterable<T>> = beEmpty(null)

fun <T> beEmptyArray(): Matcher<Array<T>> = object : Matcher<Array<T>> {
   override fun test(value: Array<T>): MatcherResult = MatcherResult(
      value.isEmpty(),
      { "Array should be empty but contained ${value.first().print().value}" },
      { "Array should not be empty" }
   )
}

internal fun <T> beEmpty(name: String?): Matcher<Iterable<T>> = object : Matcher<Iterable<T>> {
   override fun test(value: Iterable<T>): MatcherResult {
      val name = name ?: value.containerName()
      val passed: Boolean
      val sizeReport: String
      // Captured from the same iterator used for the emptiness check so dynamic iterables
      // (whose iterator() may return different elements on subsequent calls) don't fail with
      // NoSuchElementException when the failure message lambda re-iterates.
      val firstValue: T?

      when (value) {
         is Collection -> {
            passed = value.isEmpty()
            sizeReport = "${value.size} elements"
            firstValue = if (passed) null else value.first()
         }

         else -> {
            val iterator = value.iterator()
            if (iterator.hasNext()) {
               passed = false
               firstValue = iterator.next()
               sizeReport = "at least one element"
            } else {
               passed = true
               firstValue = null
               sizeReport = "0 elements"
            }
         }
      }
      return MatcherResult(
         passed,
         { "$name should be empty but has $sizeReport, first being: ${firstValue.print().value}" },
         { "$name should not be empty" }
      )
   }
}

private fun fail(name: String): Nothing {
   invokeMatcher(null, Matcher.failure("Expected $name but was null"))
   throw NotImplementedError()
}
