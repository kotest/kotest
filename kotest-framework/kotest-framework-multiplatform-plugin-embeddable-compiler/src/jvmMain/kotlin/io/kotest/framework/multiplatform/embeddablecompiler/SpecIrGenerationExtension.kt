package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.isWasm
import org.jetbrains.kotlin.platform.konan.isNative

class SpecIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
      val platform = pluginContext.platform

      val transformer = when {
         platform.isJs() || platform.isWasm() -> JsTransformer(messageCollector, pluginContext)
         platform.isNative() -> NativeTransformer(messageCollector, pluginContext)
         else -> throw UnsupportedOperationException("Cannot use Kotest compiler plugin with platform: $platform")
      }

      moduleFragment.transform(transformer, null)
   }
}
