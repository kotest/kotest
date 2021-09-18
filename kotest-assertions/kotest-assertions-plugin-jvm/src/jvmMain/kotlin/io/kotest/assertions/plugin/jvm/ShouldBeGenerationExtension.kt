package io.kotest.assertions.plugin.jvm

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class ShouldBeGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {
         var configs = mutableListOf<IrClass>()

         override fun visitClassNew(declaration: IrClass): IrStatement {
            return super.visitClassNew(declaration)
         }

         override fun visitFileNew(declaration: IrFile): IrFile {
            return super.visitFileNew(declaration)
         }

      }, null)
   }
}
