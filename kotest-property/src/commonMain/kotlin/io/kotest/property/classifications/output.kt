package io.kotest.property.classifications

import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.PropertyResult

fun PropertyContext.outputClassifications(inputs: Int, config: PropTestConfig, seed: Long) {
   val result =
      PropertyResult(List(inputs) { it.toString() }, seed, attempts(), successes(), failures(), autoclassifications())
   if (config.outputClassifications) config.classificationReporter.output(result)
}
