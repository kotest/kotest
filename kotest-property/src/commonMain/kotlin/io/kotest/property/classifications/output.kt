package io.kotest.property.classifications

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyResult
import io.kotest.property.PropertyTesting

fun PropertyContext.outputClassifications(inputs: Int, config: PropTestConfig, seed: Long) {
   val result =
      PropertyResult(List(inputs) { it.toString() }, seed, attempts(), successes(), failures(), autoclassifications())
   val enabled = config.outputLabels ?: PropertyTesting.outputClassifiations
   if (enabled) config.labelsReporter.output(result)
}
