package io.kotest.matchers.types

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Asserts that the hash code of this object is the same as the hash code of [other].
 *
 * Verifies that the result of `this.hashCode()` is equal to `other.hashCode()`.
 * This assertion does not check for reference or structural equality-only that the two objects produce
 * the same hash code value.
 *
 * Opposite of [Any.shouldNotHaveSameHashCodeAs].
 *
 * ```
 * "abc".shouldHaveSameHashCodeAs("abc")       // Assertion passes (same hashCode)
 * "abc".shouldHaveSameHashCodeAs("cba")       // May pass or fail depending on hashCode collision
 * 123.shouldHaveSameHashCodeAs(123)           // Assertion passes
 * ```
 *
 * @see [Any.shouldNotHaveSameHashCodeAs]
 */
infix fun Any.shouldHaveSameHashCodeAs(other: Any) = this should haveSameHashCodeAs(other)

/**
 * Asserts that the hash code of this object is NOT the same as the hash code of [other].
 *
 * Verifies that the result of `this.hashCode()` is not equal to `other.hashCode()`.
 * This assertion only compares hash codes-it does not assert inequality in content or reference.
 *
 * Opposite of [Any.shouldHaveSameHashCodeAs].
 *
 * ```
 * "abc".shouldNotHaveSameHashCodeAs("def")    // Assertion usually passes
 * "abc".shouldNotHaveSameHashCodeAs("abc")    // Assertion fails (same hashCode)
 * ```
 *
 * @see [Any.shouldHaveSameHashCodeAs]
 */
infix fun Any.shouldNotHaveSameHashCodeAs(other: Any) = this shouldNot haveSameHashCodeAs(other)

/**
 * Matcher that compares hash codes of two objects.
 *
 * Verifies that two objects have the same hash code using their `hashCode()` implementations.
 * This does **not** imply the objects are equal in content or identity-only that the hash codes match.
 *
 * ```
 * val a = "test"
 * val b = String(charArrayOf('t', 'e', 's', 't'))
 *
 * a should haveSameHashCodeAs(b)     // Assertion passes (same hashCode)
 *
 * val x = "abc"
 * val y = "xyz"
 *
 * x shouldNot haveSameHashCodeAs(y)  // Assertion passes (different hashCodes)
 * ```
 *
 * @see [Any.shouldHaveSameHashCodeAs]
 * @see [Any.shouldNotHaveSameHashCodeAs]
 */
fun haveSameHashCodeAs(other: Any): Matcher<Any> = object : Matcher<Any> {
   override fun test(value: Any): MatcherResult {
      return MatcherResult(
         value.hashCode() == other.hashCode(),
         { "Value $value should have hash code ${other.hashCode()}" },
         { "Value $value should not have hash code ${other.hashCode()}" }
      )
   }
}

