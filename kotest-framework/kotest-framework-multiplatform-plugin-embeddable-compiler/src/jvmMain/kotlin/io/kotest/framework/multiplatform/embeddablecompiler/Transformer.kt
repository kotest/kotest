@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.toLogger
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import java.util.concurrent.CopyOnWriteArrayList

abstract class Transformer(
   private val messageCollector: MessageCollector,
   protected val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {
   private val specs = CopyOnWriteArrayList<IrClass>()
   private var configs = CopyOnWriteArrayList<IrClass>()

   override fun visitClassNew(declaration: IrClass): IrStatement {
      super.visitClassNew(declaration)
      if (declaration.isInstantiableProjectConfig()) configs.add(declaration)
      return declaration
   }

   override fun visitFileNew(declaration: IrFile): IrFile {
      super.visitFileNew(declaration)
      val specs = declaration.specs()
      messageCollector.toLogger()
         .log("${declaration.name} contains ${specs.size} spec(s): ${specs.joinToString(", ") { it.kotlinFqName.asString() }}")
      this.specs.addAll(specs)
      return declaration
   }

   override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
      val fragment = super.visitModuleFragment(declaration)

      messageCollector.toLogger().log("Detected ${configs.size} configs:")
      configs.forEach {
         messageCollector.toLogger().log(it.kotlinFqName.asString())
      }

      messageCollector.toLogger().log("Detected ${specs.size} JS specs:")
      specs.forEach {
         messageCollector.toLogger().log(it.kotlinFqName.asString())
      }

      if (specs.isEmpty()) {
         return fragment
      }

      val file = declaration.files.first()
      val launcher = generateLauncher(specs, configs, file)
      file.addChild(launcher)

      return fragment
   }

   abstract fun generateLauncher(
      specs: Iterable<IrClass>,
      configs: Iterable<IrClass>,
      declarationParent: IrDeclarationParent
   ): IrDeclaration

   protected fun IrBuilderWithScope.callLauncher(
      launchFunction: IrSimpleFunctionSymbol,
      specs: Iterable<IrClass>,
      configs: Iterable<IrClass>,
      constructorGenerator: IrBuilderWithScope.() -> IrExpression
   ): IrCall {
      return irCall(launchFunction).also { promise: IrCall ->
         promise.dispatchReceiver = irCall(withSpecsFn).also { withSpecs ->
            withSpecs.putValueArgument(
               0,
               irVararg(
                  pluginContext.irBuiltIns.stringType,
                  specs.map { irCall(it.constructors.first()) }
               )
            )
            withSpecs.dispatchReceiver = irCall(withPlatformFn).also { withPlatform ->
               val config = configs.firstOrNull()
               if (config != null) {
                  withPlatform.dispatchReceiver = irCall(withProjectConfigFn).also { withConfig ->
                     withConfig.putValueArgument(
                        0,
                        irCall(config.constructors.first()),
                     )
                     withConfig.dispatchReceiver = constructorGenerator()
                  }
               } else {
                  withPlatform.dispatchReceiver = constructorGenerator()
               }
            }
         }
      }
   }

   protected val launcherClass by lazy {
      pluginContext.referenceClass(ClassId.fromString(EntryPoint.TestEngineClassName))
         ?: error("Cannot find ${EntryPoint.TestEngineClassName} class reference")
   }

   protected val launcherConstructor by lazy { launcherClass.constructors.first { it.owner.valueParameters.isEmpty() } }

   protected abstract val withPlatformMethodName: String

   private val withPlatformFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(withPlatformMethodName)
         ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")
   }

   private val withSpecsFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithSpecsMethodName)
         ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")
   }

   private val withProjectConfigFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WITH_PROJECT_CONFIG_METHOD_NAME)
         ?: error("Cannot find function ${EntryPoint.WITH_PROJECT_CONFIG_METHOD_NAME}")
   }
}
