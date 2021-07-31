package io.kotest.framework.multiplatform.js

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.toLogger
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrStatementsBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
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
      val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      IrGenerationExtension.registerExtension(project, SpecIrGenerationExtension(messageCollector))
   }
}

class SpecIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {
   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         val specs = mutableListOf<IrClass>()

         // we use a public val to register each spec
         val entryPointPropertyName = "kotestEngineInit"

         override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
            val fragment = super.visitModuleFragment(declaration)
            if (specs.isEmpty()) return fragment

            val file = declaration.files.first()
            messageCollector.toLogger().warning("Will write to file ${file.name}")

            val launcherClass = pluginContext.referenceClass(FqName("io.kotest.engine.TestEngineLauncher"))
               ?: error("Cannot find TestEngineLauncher class reference")
            val applyFn = pluginContext.referenceFunctions(FqName("kotlin.apply")).single()

            val funPrintln = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
               .single {
                  val parameters = it.owner.valueParameters
                  parameters.size == 1 && parameters[0].type == pluginContext.irBuiltIns.anyNType
               }

            val launcher = pluginContext.irFactory.buildProperty {
               name = Name.identifier("launcher")
            }.apply {
               parent = file

               backingField = pluginContext.irFactory.buildField {
                  type = pluginContext.irBuiltIns.unitType
                  isFinal = true
                  isExternal = false
                  isStatic = true // top level vals must be static
                  name = Name.identifier("launcher")
               }.also { field ->
                  field.correspondingPropertySymbol = this@apply.symbol
                  field.initializer = pluginContext.irFactory.createExpressionBody(startOffset, endOffset) {
                     this.expression = DeclarationIrBuilder(pluginContext, field.symbol).irBlock {
                        +irCall(launcherClass.getSimpleFunction("launch")!!).also { launch ->
                           launch.dispatchReceiver =
                              irCall(launcherClass.getSimpleFunction("register")!!).also { register ->
                                 register.dispatchReceiver = irCall(launcherClass.constructors.first())
                                 register.putValueArgument(
                                    0,
                                    irVararg(pluginContext.irBuiltIns.stringType, specs.map {
                                       irCall(it.constructors.first())
                                    })
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
                     +irCall(funPrintln).also {
                        it.putValueArgument(0, irString(specs.map { "a" }.toString()))
                     }
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

val IrPluginContext.javascriptEntryPointFn: IrSimpleFunctionSymbol
   get() = referenceFunctions(FqName("io.kotest.engine.javascriptEntryPoint")).single {
      val parameters = it.owner.valueParameters
      parameters.size == 1
   }

val IrPluginContext.arrayListClass: IrClassSymbol
   get() = referenceClass(FqName("kotlin.collections.ArrayList")) ?: error("Cannot find ArrayList class ref")

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
