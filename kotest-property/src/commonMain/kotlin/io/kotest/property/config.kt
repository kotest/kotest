package io.kotest.property

import io.kotest.assertions.readSystemProperty

/**
 * Global object for containing settings for property testing.
 */
object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = readSystemProperty("kotest.propertytest.output.generated-values", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = readSystemProperty("kotest.propertytest.output.shrink-steps", "true") == "true"
}
