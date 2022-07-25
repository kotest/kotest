package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class JsTransformer(messageCollector: MessageCollector, pluginContext: IrPluginContext) : Transformer(messageCollector, pluginContext) {
   override fun generateLauncher(specs: Iterable<IrClass>, configs: Iterable<IrClass>, declarationParent: IrDeclarationParent): IrDeclaration {
      val main = pluginContext.irFactory.buildFun {
         name = Name.identifier("main")
         returnType = pluginContext.irBuiltIns.unitType
      }.also { func: IrSimpleFunction ->
         func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
            +irCall(promiseFn).also { promise: IrCall ->
               promise.dispatchReceiver = irCall(withSpecsFn).also { withSpecs ->
                  withSpecs.putValueArgument(
                     0,
                     irVararg(
                        pluginContext.irBuiltIns.stringType,
                        specs.map { irCall(it.constructors.first()) }
                     )
                  )
                  withSpecs.dispatchReceiver = irCall(withConfigFn).also { withConfig ->
                     withConfig.putValueArgument(
                        0,
                        irVararg(
                           pluginContext.irBuiltIns.stringType,
                           configs.map { irCall(it.constructors.first()) }
                        )
                     )
                     withConfig.dispatchReceiver = irCall(launcherConstructor)
                  }
               }
            }
         }
      }

      return main
   }

   private val promiseFn = launcherClass.getSimpleFunction(EntryPoint.PromiseMethodName)
      ?: error("Cannot find function ${EntryPoint.PromiseMethodName}")
}
