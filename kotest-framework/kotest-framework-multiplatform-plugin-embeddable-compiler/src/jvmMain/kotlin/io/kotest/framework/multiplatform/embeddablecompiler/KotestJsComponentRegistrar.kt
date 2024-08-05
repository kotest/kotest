package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.toLogger
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KotestJsComponentRegistrar : CompilerPluginRegistrar() {
   override val supportsK2 = true
   override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
      val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      messageCollector.toLogger().log("Installing Kotest SpecIrGenerationExtension")
      IrGenerationExtension.registerExtension(SpecIrGenerationExtension(messageCollector))
   }
}


