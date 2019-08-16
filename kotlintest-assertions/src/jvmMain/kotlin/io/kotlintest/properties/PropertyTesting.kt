package io.kotlintest.properties

object PropertyTesting {
  var shouldPrintGeneratedValues = System.getProperty("kotlintest.propertytest.output.generator")?.toBoolean() ?: false
  var shouldPrintShrinkSteps = System.getProperty("kotlintest.propertytest.output.shrinker")?.toBoolean() ?: true
}