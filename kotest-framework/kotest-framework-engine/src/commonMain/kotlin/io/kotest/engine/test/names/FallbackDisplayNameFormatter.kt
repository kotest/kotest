package io.kotest.engine.test.names

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.engine.names.DisplayNameFormatter
import kotlin.reflect.KClass

class FallbackDisplayNameFormatter(
   private val custom: DisplayNameFormatter?,
   private val fallback: DefaultDisplayNameFormatter,
) {

   companion object {
      fun default() = default(null)

      fun default(config: AbstractProjectConfig?) =
         FallbackDisplayNameFormatter(
            DefaultDisplayNameFormatter(
               ProjectConfigResolver(config = config, registry = DefaultExtensionRegistry()),
               TestConfigResolver(config, DefaultExtensionRegistry())
            )
         )
   }

   constructor(fallback: DefaultDisplayNameFormatter) : this(null, fallback)

   fun format(kclass: KClass<*>): String {
      return custom?.format(kclass) ?: fallback.format(kclass)
   }

   fun format(testCase: TestCase): String {
      return custom?.format(testCase) ?: fallback.format(testCase)
   }
}
