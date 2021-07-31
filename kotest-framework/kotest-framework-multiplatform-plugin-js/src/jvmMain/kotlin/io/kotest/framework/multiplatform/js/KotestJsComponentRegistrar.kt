package io.kotest.framework.multiplatform.js

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
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
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KotestJsComponentRegistrar : ComponentRegistrar {

   override fun registerProjectComponents(
      project: MockProject,
      configuration: CompilerConfiguration
   ) {
      // val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      IrGenerationExtension.registerExtension(project, SpecIrGenerationExtension())
   }
}

class SpecIrGenerationExtension : IrGenerationExtension {

   companion object {
      // we use a public val to register each spec
      const val LauncherValName = "launcher"

      // the method invoked to start the tests, must exist on TestEngineLauncher
      const val LaunchMethodName = "launch"

      // the FQN for the class used to launch the MPP engine
      const val TestEngineClassName = "io.kotest.engine.TestEngineLauncher"

      // the method invoked to add specs to the launcher, must exist on TestEngineLauncher
      const val RegisterMethodName = "register"
   }

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         val specs = mutableListOf<IrClass>()

         override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
            val fragment = super.visitModuleFragment(declaration)
            if (specs.isEmpty()) return fragment

            val file = declaration.files.first()

            val launcherClass = pluginContext.referenceClass(FqName(TestEngineClassName))
               ?: error("Cannot find $TestEngineClassName class reference")

            val launchFn = launcherClass.getSimpleFunction(LaunchMethodName)
               ?: error("Cannot find function $LaunchMethodName")

            val registerFn = launcherClass.getSimpleFunction(RegisterMethodName)
               ?: error("Cannot find function $RegisterMethodName")

            val launcher = pluginContext.irFactory.buildProperty {
               name = Name.identifier(LauncherValName)
            }.apply {
               parent = file

               backingField = pluginContext.irFactory.buildField {
                  type = pluginContext.irBuiltIns.unitType
                  isFinal = true
                  isExternal = false
                  isStatic = true // top level vals must be static
                  name = Name.identifier(LauncherValName)
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

private val specClasses = listOf(
   "io.kotest.core.spec.style.BehaviorSpec",
   "io.kotest.core.spec.style.DescribeSpec",
   "io.kotest.core.spec.style.ExpectSpec",
   "io.kotest.core.spec.style.FeatureSpec",
   "io.kotest.core.spec.style.FreeSpec",
   "io.kotest.core.spec.style.FunSpec",
   "io.kotest.core.spec.style.ShouldSpec",
   "io.kotest.core.spec.style.StringSpec",
   "io.kotest.core.spec.style.WordSpec",
)

/**
 * Returns any specs declared at the top level in this file.
 */
private fun IrFile.specs() = declarations.filterIsInstance<IrClass>().filter { it.isSpecClass() }

/**
 * Recursively returns all supertypes for an [IrClass] to the top of the type tree.
 */
private fun IrClass.superTypes(): List<IrType> =
   this.superTypes + this.superTypes.flatMap { it.getClass()?.superTypes() ?: emptyList() }

/**
 * Returns true if any of the parents of this class are a spec class.
 */
private fun IrClass.isSpecClass() =
   superTypes().mapNotNull { it.classFqName?.asString() }.intersect(specClasses).isNotEmpty()
