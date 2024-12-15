package io.kotest.engine.test.names

import io.kotest.core.config.ProjectConfiguration
import io.kotest.engine.names.DisplayNameFormatter
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

class FallbackDisplayNameFormatter(
  private val custom: DisplayNameFormatter?,
  private val fallback: DefaultDisplayNameFormatter,
) {

   companion object {
      fun default() = default(ProjectConfiguration())

      fun default(configuration: ProjectConfiguration) =
         FallbackDisplayNameFormatter(DefaultDisplayNameFormatter(configuration))
   }

   constructor(fallback: DefaultDisplayNameFormatter) : this(null, fallback)

   fun format(kclass: KClass<*>): String {
      return custom?.format(kclass) ?: fallback.format(kclass)
   }

   fun format(testCase: TestCase): String {
      return custom?.format(testCase) ?: fallback.format(testCase)
   }
}
