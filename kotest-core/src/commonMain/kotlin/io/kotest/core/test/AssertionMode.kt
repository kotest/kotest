package io.kotest.core.test

import io.kotest.core.spec.SpecConfiguration

/**
 * AssertionMode is used to detect and warn a developer that a test does not execute any assertions.
 * It is usually the case that if a test function does not execute some kind of assertion then the test
 * is probably erroneous (see note). It is common to see junior developers write a test that does not actually test
 * anything.
 *
 * Therefore by setting [AssertionMode] to [Error] or [Warn], the lack of assertions in a test will cause
 * the test to fail, or a warning to be outputted respectively.
 *
 * The default value of [None] is the status quo - absense of assertions will cause no issues.
 *
 * Note: There are valid use cases for not having an assertion. For example, testing that some code will
 * compile is a valid use case, and the successful return of the compiler is sufficient test.
 */
enum class AssertionMode {
   Error, Warn, None
}

fun SpecConfiguration.resolvedAssertionMode(): AssertionMode =
   this.assertions ?: this.assertionMode() ?: AssertionMode.None // todo add project mode
