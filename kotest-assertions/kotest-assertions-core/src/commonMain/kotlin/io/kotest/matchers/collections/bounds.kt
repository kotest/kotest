package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should

// BooleanArray left out in the interest of reducing API bloat.
// As Boolean only has 2 values, it is more natural to use
// "shouldContain true / shouldContain false"-type assertions

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun ByteArray.shouldHaveUpperBound(value: Byte): ByteArray {
   asList() should haveUpperBound(value, "ByteArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun ShortArray.shouldHaveUpperBound(value: Short): ShortArray {
   asList() should haveUpperBound(value, "ShortArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun CharArray.shouldHaveUpperBound(value: Char): CharArray {
   asList() should haveUpperBound(value, "CharArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun IntArray.shouldHaveUpperBound(value: Int): IntArray {
   asList() should haveUpperBound(value, "IntArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun LongArray.shouldHaveUpperBound(value: Long): LongArray {
   asList() should haveUpperBound(value, "LongArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun FloatArray.shouldHaveUpperBound(value: Float): FloatArray {
   asList() should haveUpperBound(value, "FloatArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun DoubleArray.shouldHaveUpperBound(value: Double): DoubleArray {
   asList() should haveUpperBound(value, "DoubleArray")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun <T : Comparable<T>> Array<T>.shouldHaveUpperBound(value: T): Array<T> {
   asList() should haveUpperBound(value, "Array")
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(value: T): C {
   this should haveUpperBound(value, null)
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
infix fun <T : Comparable<T>, I : Iterable<T>> I.shouldHaveUpperBound(value: T): I {
   this should haveUpperBound(value, null)
   return this
}

/**
 * Verifies that all elements are less than or equal to [value].
 *
 * Passes if `this` is empty.
 */
fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(value: T): Matcher<C> = haveUpperBound(value, null)

private fun <T : Comparable<T>, I : Iterable<T>> haveUpperBound(t: T, name: String?): Matcher<I> = object : Matcher<I> {
   override fun test(value: I): MatcherResult {
      val name = name ?: value.containerName()
      val violatingElements = value.filter { it > t }
      return MatcherResult(
         violatingElements.isEmpty(),
         { "$name should have upper bound $t, but the following elements are above it: ${violatingElements.print().value}" },
         { "$name should not have upper bound $t" })
   }
}

private fun Iterable<*>.containerName(): String {
   return when (this) {
      is List -> "List"
      is Set -> "Set"
      is Map<*, *> -> "Map"
      is ClosedRange<*>, is OpenEndRange<*> -> "Range"
      is Collection -> "Collection"
      else -> "Iterable"
   }
}

infix fun <T : Comparable<T>> Array<T>.shouldHaveLowerBound(t: T): Array<T> {
   asList() should haveLowerBound(t, "Array")
   return this
}

infix fun <T : Comparable<T>, I : Iterable<T>> I.shouldHaveLowerBound(t: T): I {
   toList() should haveLowerBound(t, null)
   return this
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T): C {
   this should haveLowerBound(t, null)
   return this
}

fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T): Matcher<C> = haveLowerBound(t, null)

private fun <T : Comparable<T>, I : Iterable<T>> haveLowerBound(t: T, name: String?): Matcher<I> = object : Matcher<I> {
   override fun test(value: I): MatcherResult {
      val name = name ?: value.containerName()
      val violatingElements = value.filter { it < t }
      return MatcherResult(
         violatingElements.isEmpty(),
         { "$name should have lower bound $t, but the following elements are below it: ${violatingElements.print().value}" },
         { "$name should not have lower bound $t" })
   }
}
