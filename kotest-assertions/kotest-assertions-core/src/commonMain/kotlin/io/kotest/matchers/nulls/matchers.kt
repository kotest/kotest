package io.kotest.matchers.nulls

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResult.Companion.invoke
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.contract

/**
 * Verifies that this value is null
 *
 * Matcher to verify that a specific value contains a reference to `null`.
 * Opposite of [shouldNotBeNull]
 *
 * Example:
 *
 * ```
 *     val nullable: String? = null
 *     val nonNull: String? = "NonNull"
 *
 *     nullable.shouldBeNull()    // Passes
 *     nonNull.shouldBeNull()     // Fails
 *
 * ```
 */
fun Any?.shouldBeNull() {
  contract {
    returns() implies (this@shouldBeNull == null)
  }

  this should beNull()
}

/**
 * Verifies that this is not null
 *
 * Matcher to verify that a specific nullable reference is not null.
 * Opposite of [shouldBeNull]
 *
 * Example:
 *
 * ```
 *     val nullable: String? = null
 *     val nonNull: String? = "NonNull"
 *
 *     nonNull.shouldNotBeNull()     // Passes
 *     nullable.shouldNotBeNull()    // Fails
 * ```
 *
 * Note: This function uses Kotlin Contracts to tell the compiler that this is not null. So after this is used, all subsequent
 * lines can assume the value is not null without having to cast it. For example:
 *
 * ```
 *
 *     val nonNull: String? = "NonNull"
 *
 *     nonNull.shouldNotBeNull()
 *     useNonNullString(nonNull)
 *
 *
 *     // Notice how this is a not-nullable reference
 *     fun useNonNullString(string: String) { }
 *
 * ```
 */
fun <T> T?.shouldNotBeNull(): T {
   contract {
      returns() implies (this@shouldNotBeNull != null)
   }

   this shouldNot beNull()
   return this!!
}

/**
 * Verifies that this is not null and lets you use its value in [block]
 *
 * maybeNullString() shouldNotBeNull {
 *     length should beEven()
 * }
 */
infix fun <T : Any> T?.shouldNotBeNull(block: T.() -> Unit): T {
   this.shouldNotBeNull()
   block()
   return this
}

/**
 * Matcher that verifies if a reference is null
 *
 * Verifies that a given value contains a reference to null or not.
 *
 * Example:
 * ```
 *     val nullable: String? = null
 *     val nonNull: String? = "NonNull"
 *
 *     nullable should beNull() // Passes
 *     nonNull should beNull()  // Fails
 *
 *     nullable shouldNot beNull() // Fails
 *     nonNull shouldNot beNull()  // Passes
 *
 * ```
 * @see [shouldBeNull]
 * @see [shouldNotBeNull]
 */
fun beNull() = object : Matcher<Any?> {

  override fun test(value: Any?): MatcherResult {
    val passed = value == null

     return invoke(
        passed,
        { "Expected value to be null, but was $value." },
        { "Expected value to not be null, but was null." }
     )
  }
}
