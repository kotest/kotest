package io.kotest.framework.js

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class KotestCommandLineProcessor : CommandLineProcessor {

   override val pluginId: String = "io.kotest.js"

   override val pluginOptions: Collection<AbstractCliOption> = emptyList()

   override fun processOption(
      option: AbstractCliOption,
      value: String,
      configuration: CompilerConfiguration
   ) = error("Unknown plugin option: ${option.optionName}")
}
