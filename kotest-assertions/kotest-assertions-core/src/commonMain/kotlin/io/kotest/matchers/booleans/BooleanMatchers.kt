package io.kotest.matchers.booleans

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Asserts that this [Boolean] is true
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is true.
 * Opposite of [Boolean.shouldNotBeTrue]
 *
 * ```
 * true.shouldBeTrue    // Assertion passes
 * false.shouldBeTrue   // Assertion fails
 * null.shouldBeTrue    // Assertion fails
 *
 * (3 + 3 == 6).shouldBeTrue    // Assertion passes
 * (3 + 3 == 42).shouldBeTrue   // Assertion fails
 * ```
 * @see [Boolean?.shouldNotBeFalse]
 * @see [Boolean?.shouldBeFalse]
 */
@OptIn(ExperimentalContracts::class)
fun Boolean?.shouldBeTrue(): Boolean {
   contract {
      returns() implies (this@shouldBeTrue != null)
   }

   this should beTrue()
   return this!!
}
/**
 * Asserts that this [Boolean] is not true
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is not true.
 * Opposite of [Boolean.shouldBeTrue]
 *
 * ```
 * false.shouldNotBeTrue   // Assertion passes
 * null.shouldNotBeTrue    // Assertion passes
 * true.shouldNotBeTrue    // Assertion fails
 *
 * (3 + 3 == 42).shouldNotBeTrue   // Assertion passes
 * (3 + 3 == 6).shouldNotBeTrue    // Assertion fails
 * ```
 * @see [Boolean?.shouldBeFalse]
 * @see [Boolean?.shouldNotBeFalse]
 */
fun Boolean?.shouldNotBeTrue(): Boolean? {
   this shouldNot beTrue()
   return this
}

/**
 * Asserts that this [Boolean] is false
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is false.
 * Opposite of [Boolean.shouldNotBeFalse]
 *
 * ```
 * false.shouldBeFalse  // Assertion passes
 * true.shouldBeFalse   // Assertion fails
 * null.shouldBeFalse  // Assertion fails
 *
 * (3 + 3 == 42).shouldBeFalse  // Assertion passes
 * (3 + 3 == 6).shouldBeFalse   // Assertion fails
 * ```
 * @see [Boolean?.shouldNotBeTrue]
 * @see [Boolean?.shouldBeTrue]
 */
@OptIn(ExperimentalContracts::class)
fun Boolean?.shouldBeFalse(): Boolean {
   contract {
      returns() implies (this@shouldBeFalse != null)
   }

   this should beFalse()
   return this!!
}

/**
 * Asserts that this [Boolean] is not false
 *
 * Verifies that this (nullable) [Boolean] or boolean expression is not false.
 * Opposite of [Boolean.shouldBeFalse]
 *
 * ```
 * true.shouldNotBeFalse   // Assertion passes
 * null.shouldNotBeFalse   // Assertion passes
 * false.shouldNotBeFalse  // Assertion fails
 *
 * (3 + 3 == 6).shouldNotBeFalse   // Assertion passes
 * (3 + 3 == 42).shouldNotBeFalse  // Assertion fails
 * ```
 * @see [Boolean?.shouldBeTrue]
 * @see [Boolean?.shouldNotBeTrue]
 */
fun Boolean?.shouldNotBeFalse(): Boolean? {
   this shouldNot beFalse()
   return this
}

/**
 * Matcher that verifies a given [Boolean]? is true.
 *
 * This is the matcher form used with `should`/`shouldNot`. It is functionally
 * equivalent to calling [Boolean?.shouldBeTrue] and the opposite of
 * [Boolean?.shouldNotBeTrue].
 *
 * ```
 * true.should(beTrue())          // passes
 * false.shouldNot(beTrue())      // passes
 * false.should(beTrue())         // fails
 * null.shouldNot(beTrue())       // passes (null is not true)
 * null.should(beTrue())          // fails
 * ```
 *
 * @see beFalse
 * @see beBoolean
 * @see Boolean?.shouldBeTrue
 * @see Boolean?.shouldNotBeTrue
 */
fun beTrue() = beBoolean(true)

/**
 * Matcher that verifies a given [Boolean] is false.
 *
 * This is the matcher form used with `should`/`shouldNot`. It is functionally
 * equivalent to calling [Boolean?.shouldBeFalse] and the opposite of
 * [Boolean?.shouldNotBeFalse].
 *
 * ```
 * false.should(beFalse())        // passes
 * true.shouldNot(beFalse())      // passes
 * true.should(beFalse())         // fails
 * null.shouldNot(beFalse())      // passes (null is not false)
 * null.should(beFalse())         // fails
 * ```
 *
 * @see beTrue
 * @see beBoolean
 * @see Boolean?.shouldBeFalse
 * @see Boolean?.shouldNotBeFalse
 */
fun beFalse() = beBoolean(false)

/**
 * Creates a matcher that verifies a [Boolean] is equal to [expected].
 *
 * Use this when you need to pass the expected boolean value dynamically; for
 * convenience, prefer [beTrue] and [beFalse] for the common cases.
 *
 * ```
 * val expected = computeFlag()
 * flag.should(beBoolean(expected))
 *
 * beBoolean(true)  // same as beTrue()
 * beBoolean(false) // same as beFalse()
 * ```
 *
 * @param expected the boolean value the actual should equal.
 * @return a [Matcher] that asserts the subject equals [expected].
 *
 * @see beTrue
 * @see beFalse
 */
fun beBoolean(expected: Boolean) = object : Matcher<Boolean?> {
   override fun test(value: Boolean?) = MatcherResult(
      value == expected,
      { "$value should equal $expected" },
      { "$value should not equal $expected" }
   )
}
