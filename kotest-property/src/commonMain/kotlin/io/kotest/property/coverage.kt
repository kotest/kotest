//package io.kotest.property
//
//import io.kotest.assertions.fail
//
//suspend fun checkCoverage(percentage: Double, label: String, f: suspend () -> PropertyContext): PropertyContext {
//   val context = f()
//   val labelled = context.classifications()[label] ?: 0
//   val attempts = context.attempts()
//   val actual = (labelled.toDouble() / attempts.toDouble()) * 100.0
//   if (actual < percentage)
//      fail("Property test required coverage of $percentage% for [$label] but was [${actual.toInt()}%]")
//   return context
//}
