package io.kotest.properties

import io.kotest.assertions.readSystemProperty

@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = readSystemProperty("kotest.propertytest.output.generated-values", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = readSystemProperty("kotest.propertytest.output.shrink-steps", "true") == "true"
}
