package io.kotlintest.properties

fun outputValues(context: PropertyContext) {
  val values = context.values().joinToString(", ")
  if (PropertyTesting.shouldPrintGeneratedValues) {
    println("Property test completed; values = [$values]")
  }
}