package io.kotest.common.errors

/**
 * Implemented by assertion errors for when an actual value did not match an expected value.
 */
interface ComparisonError {
   val expectedValue: String
   val actualValue: String
}

