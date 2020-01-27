package io.kotest.core

import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.getAndReset
import io.kotest.core.config.Project
import io.kotest.core.test.AssertionMode

/**
 * Executes the given run function checking for the absense of assertions according to
 * the receiver [AssertionMode].
 *
 * @throws ZeroAssertionsError if the mode is [AssertionMode.Error] and no assertions were executed.
 */
suspend fun AssertionMode.executeWithAssertionsCheck(run: suspend () -> Unit, name: String) {
   AssertionCounter.reset()

   executeWithGlobalAssertSoftlyCheck(run)
   val warningMessage = "Test '${name}' did not invoke any assertions"

   if (AssertionCounter.getAndReset() == 0) {
      when (this) {
         AssertionMode.Error -> throw ZeroAssertionsError(warningMessage)
         AssertionMode.Warn -> println("Warning: $warningMessage")
         AssertionMode.None -> Unit
      }
   }
}


suspend fun executeWithGlobalAssertSoftlyCheck(run: suspend () -> Unit) {
   if (Project.globalAssertSoftly()) {
      assertSoftly {
         run()
      }
   } else {
      run()
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
