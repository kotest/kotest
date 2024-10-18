package io.kotest.property.core

import io.kotest.assertions.print.print
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext

internal fun buildMaxFailureErrorMessage(
   context: PropertyContext,
   config: PropTestConfig,
   inputs: List<Any?>,
): String {
   return buildString {
      appendLine("Property failed ${context.failures()} times (maxFailure rate was ${config.maxFailure})")
      appendLine("Last error was caused by args:")
      inputs.withIndex().forEach { (index, value) ->
         appendLine("  $index) ${value.print().value}")
      }
   }
}
