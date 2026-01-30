package io.kotest.matchers.string

import io.kotest.matchers.should
import io.kotest.matchers.shouldNot


/**
 * Verifies that the given [String] contains all the specified substrings in given order,
 * with any characters before, after, or in between.
 *
 * For example, each of the following examples would pass:
 *
 * val value = "The quick brown fox jumps over the lazy dog"
 * value.shouldContainInOrderWithoutOverlaps("The", "quick", "fox", "jumps", "over", "dog")
 * value.shouldContainInOrderWithoutOverlaps("The quick", "fox jump", "over", "dog")
 * "superstar".shouldContainInOrderWithoutOverlaps("super", "star")
 *
 * Note: unlike [shouldContainInOrder], consecutive substrings cannot overlap.
 * So the following tests would fail:
 *
 * "sourdough bread".shouldContainInOrderWithoutOverlaps("bread", "read")
 * "superstar".shouldContainInOrderWithoutOverlaps("supers", "star")
 */
fun String?.shouldContainInOrderWithoutOverlaps(vararg substrings: String): String? {
   this should containInOrderWithoutOverlaps(*substrings)
   return this
}

fun String?.shouldNotContainInOrderWithoutOverlaps(vararg substrings: String): String? {
   this shouldNot containInOrderWithoutOverlaps(*substrings)
   return this
}

fun containInOrderWithoutOverlaps(vararg substrings: String) = containInOrder(*substrings)

