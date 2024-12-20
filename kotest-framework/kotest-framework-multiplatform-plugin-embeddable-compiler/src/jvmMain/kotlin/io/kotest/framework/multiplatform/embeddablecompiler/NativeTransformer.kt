package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
class NativeTransformer(messageCollector: MessageCollector, pluginContext: IrPluginContext) :
   Transformer(messageCollector, pluginContext) {

   override fun generateLauncher(
      specs: Iterable<IrClass>,
      configs: Iterable<IrClass>,
      declarationParent: IrDeclarationParent
   ): IrDeclaration {
      val launcher = pluginContext.irFactory.buildProperty {
         name = Name.identifier(EntryPoint.LauncherValName)
         visibility = DescriptorVisibilities.PRIVATE
      }.apply {
         parent = declarationParent
         annotations += IrSingleStatementBuilder(
            pluginContext,
            Scope(this.symbol),
            UNDEFINED_OFFSET,
            UNDEFINED_OFFSET
         ).build { irCall(eagerAnnotationConstructor) }

         backingField = pluginContext.irFactory.buildField {
            type = pluginContext.irBuiltIns.unitType
            isFinal = true
            isExternal = false
            isStatic = true // top level vals must be static
            name = Name.identifier(EntryPoint.LauncherValName)
         }.also { field ->
            field.correspondingPropertySymbol = this@apply.symbol
            field.initializer = pluginContext.irFactory.createExpressionBody(
               startOffset,
               endOffset,
               DeclarationIrBuilder(pluginContext, field.symbol).irBlock {
                  +callLauncher(launchFn, specs, configs) {
                     irCall(withTeamCityListenerMethodNameFn).also { withTeamCity ->
                        withTeamCity.dispatchReceiver = irCall(launcherConstructor)
                     }
                  }
               })
         }

         addGetter {
            returnType = pluginContext.irBuiltIns.unitType
         }.also { func ->
            func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
            }
         }
      }

      return launcher
   }

   override val withPlatformMethodName: String = EntryPoint.WithNativeMethodName

   private val launchFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.LaunchMethodName)
         ?: error("Cannot find function ${EntryPoint.LaunchMethodName}")
   }

   private val withTeamCityListenerMethodNameFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithTeamCityListenerMethodName)
         ?: error("Cannot find function ${EntryPoint.WithTeamCityListenerMethodName}")
   }

   private val eagerAnnotationConstructor by lazy {
      val annotationName = ClassId.fromString("kotlin/native/EagerInitialization")

      val annotation = pluginContext.referenceClass(annotationName)
         ?: error("Cannot find eager initialisation annotation class $annotationName")

      annotation.constructors.single()
   }
}
