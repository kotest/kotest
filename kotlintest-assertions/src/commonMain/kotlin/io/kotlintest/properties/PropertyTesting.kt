package io.kotlintest.properties

import io.kotlintest.assertions.readSystemProperty

object PropertyTesting {
  var shouldPrintGeneratedValues: Boolean = readSystemProperty("kotlintest.propertytest.output.generator", "false") == "true"
  var shouldPrintShrinkSteps: Boolean = readSystemProperty("kotlintest.propertytest.output.generator", "false") == "false"
}
