package io.kotest.properties

import io.kotest.mpp.sysprop

@Deprecated("Deprecated and will be removed in 4.2. Migrate to the new property test classes in 4.0")
object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = sysprop("kotest.propertytest.output.generated-values", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = sysprop("kotest.propertytest.output.shrink-steps", "true") == "true"
}
