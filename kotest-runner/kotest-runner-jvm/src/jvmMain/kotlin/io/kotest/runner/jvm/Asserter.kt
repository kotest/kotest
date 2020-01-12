package io.kotest.runner.jvm

import io.kotest.Project
import io.kotest.assertSoftly
import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.getAndReset
import io.kotest.core.test.AssertionMode

/**
 * Executes the given run function checking for the absense of assertions according to
 * the given [AssertionMode].
 *
 * @return a Throwable if the assertion mode is [Error] and no assertions were executed else null
 */
inline fun collectAssertions(run: () -> Unit, name: String, mode: AssertionMode): Throwable? {
   AssertionCounter.reset()

   if (Project.globalAssertSoftly()) {
      assertSoftly {
         run()
      }
   } else {
      run()
   }

   val warningMessage = "Test '${name}' did not invoke any assertions"

   return if (AssertionCounter.getAndReset() > 0) null else {
      when (mode) {
         AssertionMode.Error -> RuntimeException(warningMessage)
         AssertionMode.Warn -> {
            println("Warning: $warningMessage")
            null
         }
         AssertionMode.None -> null
      }
   }
}
