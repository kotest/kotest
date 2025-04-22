package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

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

      // this generates the runTest function which sets up the engine and launches it
      // on JS we launch using the .promise() method which wraps in a GlobalScope.promise which should make node wait
      val main = pluginContext.irFactory.buildFun {
         name = Name.identifier(EntryPoint.RUN_TESTS_FUNCTION_NAME)
         returnType = pluginContext.irBuiltIns.unitType
         visibility = DescriptorVisibilities.PUBLIC
         modality = Modality.FINAL
      }.also { func: IrSimpleFunction ->
         // we must make this function with @JsExport so its available as a function that can be called
         // from regular javascript without name mangling.
         func.annotations = listOf(
            DeclarationIrBuilder(pluginContext, func.symbol).irCallConstructor(jsExportConstructor(), emptyList())
         )
         // the body of this function ultimately calls promise.
         func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
            +callLauncher(promiseFn(), specs, configs) {
               irCall(launcherConstructor)
            }
         }
      }

      return main
   }

   @OptIn(UnsafeDuringIrConstructionAPI::class)
   private fun jsExportConstructor(): IrConstructorSymbol {
      val jsExportAnnotation = pluginContext
         .referenceClass(ClassId.fromString(EntryPoint.JS_EXPORT_ANNOTATION_CLASS_NAME))
         ?: error("Can't find js export")

      return jsExportAnnotation.constructors.first()
   }

   @OptIn(UnsafeDuringIrConstructionAPI::class)
   private fun promiseFn(): IrSimpleFunctionSymbol {
      return launcherClass.getSimpleFunction(EntryPoint.PROMISE_FUNCTION_NAME)
         ?: error("Cannot find function ${EntryPoint.PROMISE_FUNCTION_NAME}")
   }
}
