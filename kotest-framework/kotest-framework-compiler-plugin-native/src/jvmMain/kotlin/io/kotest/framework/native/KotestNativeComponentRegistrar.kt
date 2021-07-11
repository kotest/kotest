package io.kotest.framework.native

import com.intellij.mock.MockProject
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class KotestNativeComponentRegistrar : ComponentRegistrar {

   override fun registerProjectComponents(
      project: MockProject,
      configuration: CompilerConfiguration
   ) {
      val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      IrGenerationExtension.registerExtension(project, SpecIrGenerationExtension(messageCollector))
   }
}
