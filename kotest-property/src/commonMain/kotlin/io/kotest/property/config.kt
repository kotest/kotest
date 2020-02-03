package io.kotest.property

import io.kotest.mpp.sysprop

/**
 * Global object for containing settings for property testing.
 */
object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", "true") == "true"
}

data class PropTestConfig(
   val seed: Long = 0,
   val minSuccess: Int = Int.MAX_VALUE,
   val maxFailure: Int = 0,
   val shrinkingMode: ShrinkingMode = ShrinkingMode.Bounded(1000)
)
