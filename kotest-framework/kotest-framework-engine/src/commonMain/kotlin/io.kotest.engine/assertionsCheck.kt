package io.kotest.engine

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.spec.Spec
import io.kotest.engine.spec.AbstractSpec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.AssertionMode.Error
import io.kotest.core.test.AssertionMode.None
import io.kotest.core.test.AssertionMode.Warn

fun Spec.resolvedAssertionMode(): AssertionMode = when (this) {
   is AbstractSpec -> this.assertions ?: this.assertionMode() ?: None // todo add project mode
   else -> None
}

/**
 * Executes the given run function checking for the absense of assertions according to
 * the receiver [AssertionMode].
 *
 * @throws ZeroAssertionsError if the mode is [AssertionMode.Error] and no assertions were executed.
 */
suspend fun AssertionMode.executeWithAssertionsCheck(name: String, run: suspend () -> Unit) {
   assertionCounter.reset()
   run()
   val warningMessage = "Test '${name}' did not invoke any assertions"

   if (assertionCounter.getAndReset() == 0) {
      when (this) {
         Error -> throw ZeroAssertionsError(warningMessage)
         Warn -> println("Warning: $warningMessage")
         None -> Unit
      }
   }
}

class ZeroAssertionsError(message: String) : AssertionError(message)
