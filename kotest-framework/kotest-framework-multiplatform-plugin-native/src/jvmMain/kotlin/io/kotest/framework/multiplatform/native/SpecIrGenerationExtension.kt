package io.kotest.framework.multiplatform.native

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class SpecIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         val specs = mutableListOf<IrClass>()

         override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
            val fragment = super.visitModuleFragment(declaration)
            if (specs.isEmpty()) return fragment

            val file = declaration.files.first()

            val launcherClass = pluginContext.referenceClass(FqName(EntryPoint.TestEngineClassName))
               ?: error("Cannot find ${EntryPoint.TestEngineClassName} class reference")

            val launchFn = launcherClass.getSimpleFunction(EntryPoint.LaunchMethodName)
               ?: error("Cannot find function ${EntryPoint.LaunchMethodName}")

            val registerFn = launcherClass.getSimpleFunction(EntryPoint.RegisterMethodName)
               ?: error("Cannot find function ${EntryPoint.RegisterMethodName}")

            val launcher = pluginContext.irFactory.buildProperty {
               name = Name.identifier(EntryPoint.LauncherValName)
            }.apply {
               parent = file

               backingField = pluginContext.irFactory.buildField {
                  type = pluginContext.irBuiltIns.unitType
                  isFinal = true
                  isExternal = false
                  isStatic = true // top level vals must be static
                  name = Name.identifier(EntryPoint.LauncherValName)
               }.also { field ->
                  field.correspondingPropertySymbol = this@apply.symbol
                  field.initializer = pluginContext.irFactory.createExpressionBody(startOffset, endOffset) {
                     this.expression = DeclarationIrBuilder(pluginContext, field.symbol).irBlock {
                        +irCall(launchFn).also { launch ->
                           launch.dispatchReceiver = irCall(registerFn).also { register ->
                              register.dispatchReceiver = irCall(launcherClass.constructors.first())
                              register.putValueArgument(
                                 0,
                                 irVararg(
                                    pluginContext.irBuiltIns.stringType,
                                    specs.map { irCall(it.constructors.first()) }
                                 )
                              )
                           }
                        }
                     }
                  }
               }

               addGetter {
                  returnType = pluginContext.irBuiltIns.unitType
               }.also { func ->
                  func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                  }
               }
            }

            file.addChild(launcher)
            return fragment
         }

         override fun visitFileNew(declaration: IrFile): IrFile {
            declaration.specs().forEach { spec ->
               specs.add(spec)
            }
            return declaration
         }

      }, null)
   }
}
