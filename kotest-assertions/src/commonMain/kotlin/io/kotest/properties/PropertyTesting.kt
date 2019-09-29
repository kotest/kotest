package io.kotest.properties

import io.kotest.assertions.readSystemProperty

object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = readSystemProperty("kotest.propertytest.output.generated-values", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = readSystemProperty("kotest.propertytest.output.shrink-steps", "true") == "true"
}
