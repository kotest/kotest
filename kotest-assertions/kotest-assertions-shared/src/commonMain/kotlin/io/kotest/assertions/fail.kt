package io.kotest.assertions

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

/**
 * Verifies that [block] throws an [AssertionError]
 *
 * If [block] throws an [AssertionError], this method will pass. Otherwise, it will throw an error, as a failure was
 * expected.
 *
 * This should be used mainly to check that an assertion fails, for example:
 *
 * ```
 *     shouldFail {
 *        1 shouldBe 2  // This should fail
 *     }
 * ```
 *
 * @returns the [AssertionError] that was thrown
 * @see shouldThrowAny
 * @see shouldThrow
 * @see shouldThrowExactly
 */
inline fun shouldFail(block: () -> Any?): AssertionError = shouldThrow(block)

/**
 * Verifies that [block] throws an [AssertionError] with the expected [message]
 *
 * If [block] throws an [AssertionError] with the given [message], this method will pass.
 * Otherwise, it will throw an error, as a failure was expected.
 *
 * This should be used mainly to check that an assertion fails, for example:
 *
 * ```
 *     shouldFail {
 *        1 shouldBe 2  // This should fail
 *     }
 * ```
 *
 * @returns the [AssertionError] that was thrown
 * @see shouldFail
 * @see shouldThrowAny
 * @see shouldThrow
 * @see shouldThrowExactly
 */
inline fun shouldFailWithMessage(message: String, block: () -> Any?): AssertionError =
   shouldFail(block).also { t ->
      t.message shouldBe message
   }

fun fail(msg: String): Nothing = throw failure(msg)
