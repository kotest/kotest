package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.ir.builders.irCallConstructor

class JsTransformer(
   override val withPlatformMethodName: String,
   messageCollector: MessageCollector,
   pluginContext: IrPluginContext
) : Transformer(messageCollector, pluginContext) {

   override fun generateLauncher(
      specs: Iterable<IrClass>,
      configs: Iterable<IrClass>,
      declarationParent: IrDeclarationParent
   ): IrDeclaration {

      val jsExportAnnotation = pluginContext.referenceClass(ClassId.fromString(EntryPoint.JsExportAnnotationClassName))
         ?: error("Can't find js export")

      val jsExportConstructor = jsExportAnnotation.constructors.first()

      val main = pluginContext.irFactory.buildFun {
         name = Name.identifier(EntryPoint.JS_ENTRY_POINT_NAME)
         returnType = pluginContext.irBuiltIns.unitType
         visibility = DescriptorVisibilities.PUBLIC
         modality = Modality.FINAL
      }.also { func: IrSimpleFunction ->
         func.annotations = listOf(
            DeclarationIrBuilder(pluginContext, func.symbol).irCallConstructor(jsExportConstructor, emptyList())
         )
         func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
            +callLauncher(promiseFn(), specs, configs) {
               irCall(launcherConstructor)
            }
         }
      }

      return main
   }

   @OptIn(UnsafeDuringIrConstructionAPI::class)
   private fun promiseFn(): IrSimpleFunctionSymbol {
      return launcherClass.getSimpleFunction(EntryPoint.PROMISE_METHOD_NAME)
         ?: error("Cannot find function ${EntryPoint.PROMISE_METHOD_NAME}")
   }
}
