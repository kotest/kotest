package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KotestJsComponentRegistrar : CompilerPluginRegistrar() {
   override val supportsK2 = true
   override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
      val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      // a better way to test for "test" source set would be preferred
      if (configuration.kotlinSourceRoots.any { it.hmppModuleName?.lowercase()?.contains("test") == true }) {
//         FirExtensionRegistrarAdapter.registerExtension(ConfigClassRegistrar(messageCollector))
         IrGenerationExtension.registerExtension(SpecIrGenerationExtension(messageCollector))
      }
   }
}


