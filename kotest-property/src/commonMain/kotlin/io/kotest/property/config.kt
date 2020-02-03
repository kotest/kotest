package io.kotest.property

import io.kotest.mpp.sysprop

/**
 * Global object for containing settings for property testing.
 */
object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = sysprop("kotest.proptest.output.generated-values", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = sysprop("kotest.proptest.output.shrink-steps", "true") == "true"
}
