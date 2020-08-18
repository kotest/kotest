package io.kotest.core.internal

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.AssertionMode.Error
import io.kotest.core.test.AssertionMode.None
import io.kotest.core.test.AssertionMode.Warn
import io.kotest.core.test.Description

internal fun Spec.resolvedAssertionMode(): AssertionMode {
   return this.assertions ?: this.assertionMode() ?: configuration.assertionMode
}

/**
 * Executes the given run function checking for the absense of assertions according to
 * the receiver [AssertionMode].
 *
 * @throws ZeroAssertionsError if the mode is [AssertionMode.Error] and no assertions were executed.
 */
internal suspend fun AssertionMode.executeWithAssertionsCheck(name: Description, run: suspend () -> Unit) {
   assertionCounter.reset()
   run()
   val warningMessage = "Test '${name.displayName()}' did not invoke any assertions"

   if (assertionCounter.getAndReset() == 0) {
      when (this) {
         Error -> throw ZeroAssertionsError(warningMessage)
         Warn -> println("Warning: $warningMessage")
         None -> Unit
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
