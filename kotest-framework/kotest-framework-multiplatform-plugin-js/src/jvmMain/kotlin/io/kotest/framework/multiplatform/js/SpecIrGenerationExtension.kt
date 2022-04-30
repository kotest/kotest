package io.kotest.framework.multiplatform.js

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.toLogger
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.util.concurrent.CopyOnWriteArrayList

class SpecIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         val specs = CopyOnWriteArrayList<IrClass>()
         var configs = CopyOnWriteArrayList<IrClass>()

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

            if (specs.isEmpty()) return fragment

            val file = declaration.files.first()

            val launcherClass = pluginContext.referenceClass(FqName(EntryPoint.TestEngineClassName))
               ?: error("Cannot find ${EntryPoint.TestEngineClassName} class reference")

            val launcherConstructor = launcherClass.constructors.first { it.owner.valueParameters.isEmpty() }

            val promiseFn = launcherClass.getSimpleFunction(EntryPoint.PromiseMethodName)
               ?: error("Cannot find function ${EntryPoint.PromiseMethodName}")

            val withSpecsFn = launcherClass.getSimpleFunction(EntryPoint.WithSpecsMethodName)
               ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")

            val withConfigFn = launcherClass.getSimpleFunction(EntryPoint.WithConfigMethodName)
               ?: error("Cannot find function ${EntryPoint.WithConfigMethodName}")

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

//            val launcher = pluginContext.irFactory.buildProperty {
//               name = Name.identifier(EntryPoint.LauncherValName)
//            }.apply {
//               parent = file
//               backingField = pluginContext.irFactory.buildField {
//                  type = pluginContext.irBuiltIns.unitType
//                  isFinal = true
//                  isExternal = false
//                  isStatic = true // top level vals must be static
//                  name = Name.identifier(EntryPoint.LauncherValName)
//               }.also { field ->
//                  field.correspondingPropertySymbol = this@apply.symbol
//                  field.initializer = pluginContext.irFactory.createExpressionBody(startOffset, endOffset) {
//                     this.expression = DeclarationIrBuilder(pluginContext, field.symbol).irBlock {
//                        +irCall(promiseFn).also { promise: IrCall ->
//                           promise.dispatchReceiver = irCall(withSpecsFn).also { withSpecs ->
//                              withSpecs.putValueArgument(
//                                 0,
//                                 irVararg(
//                                    pluginContext.irBuiltIns.stringType,
//                                    specs.map { irCall(it.constructors.first()) }
//                                 )
//                              )
//                              withSpecs.dispatchReceiver = irCall(withConfigFn).also { withConfig ->
//                                 withConfig.putValueArgument(
//                                    0,
//                                    irVararg(
//                                       pluginContext.irBuiltIns.stringType,
//                                       configs.map { irCall(it.constructors.first()) }
//                                    )
//                                 )
//                                 withConfig.dispatchReceiver = irCall(launcherConstructor)
//                              }
//                           }
//                        }
//                     }
//                  }
//               }
//
//               addGetter {
//                  returnType = pluginContext.irBuiltIns.unitType
//               }.also { func: IrSimpleFunction ->
//                  func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
//                  }
//               }
//            }

            file.addChild(main)
            return fragment
         }

         override fun visitClassNew(declaration: IrClass): IrStatement {
            super.visitClassNew(declaration)
            if (declaration.isProjectConfig()) configs.add(declaration)
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

      }, null)
   }
}
