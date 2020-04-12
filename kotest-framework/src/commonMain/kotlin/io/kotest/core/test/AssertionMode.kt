package io.kotest.core.test

import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.spec.Spec
import io.kotest.core.test.AssertionMode.Error
import io.kotest.core.test.AssertionMode.None
import io.kotest.core.test.AssertionMode.Warn

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

fun Spec.resolvedAssertionMode(): AssertionMode =
   this.assertions ?: this.assertionMode() ?: None // todo add project mode

/**
 * Executes the given run function checking for the absense of assertions according to
 * the receiver [AssertionMode].
 *
 * @throws ZeroAssertionsError if the mode is [AssertionMode.Error] and no assertions were executed.
 */
suspend fun AssertionMode.executeWithAssertionsCheck(name: String, run: suspend () -> Unit) {
   AssertionCounter.reset()
   run()
   val warningMessage = "Test '${name}' did not invoke any assertions"

   if (AssertionCounter.getAndReset() == 0) {
      when (this) {
         Error -> throw ZeroAssertionsError(warningMessage)
         Warn -> println("Warning: $warningMessage")
         None -> Unit
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
