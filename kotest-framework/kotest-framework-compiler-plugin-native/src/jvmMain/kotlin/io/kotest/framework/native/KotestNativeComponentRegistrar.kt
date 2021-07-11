package io.kotest.framework.native

import com.intellij.mock.MockProject
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class KotestNativeComponentRegistrar : ComponentRegistrar {

   override fun registerProjectComponents(
      project: MockProject,
      configuration: CompilerConfiguration
   ) {
      IrGenerationExtension.registerExtension(project, SpecIrGenerationExtension())
   }
}
