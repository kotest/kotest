package io.kotest.framework.js

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
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
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.kotlinFqName
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

         override fun visitFileNew(declaration: IrFile): IrFile {
            declaration.specs().forEach { spec ->

               // we use a public val to register each spec, so this name must be unique across all files
               // therefore using FQN spec name seems a safe bet
               val entryPointPropertyName = "kotestSpecEntryPoint_${spec.kotlinFqName.asString().replace(".", "_")}"

               val registerKotestProperty = pluginContext.irFactory.buildProperty {
                  name = Name.identifier(entryPointPropertyName)
               }.apply {
                  parent = declaration
                  backingField = pluginContext.irFactory.buildField {
                     type = pluginContext.irBuiltIns.unitType
                     isFinal = true
                     isExternal = false
                     isStatic = true // top level vals must be static
                     name = Name.identifier(entryPointPropertyName)
                  }.also {
                     it.correspondingPropertySymbol = this@apply.symbol
                     it.initializer = pluginContext.irFactory.createExpressionBody(startOffset, endOffset) {
                        this.expression = DeclarationIrBuilder(pluginContext, it.symbol).irBlock {
                           invokeSpec(pluginContext, spec)
                        }
                     }
                  }

                  addGetter {
                     returnType = pluginContext.irBuiltIns.unitType
                  }.also { func ->
                     func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                        invokeSpec(pluginContext, spec)
                     }
                  }
               }

               declaration.addChild(registerKotestProperty)
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

val IrPluginContext.executeSpecFn: IrSimpleFunctionSymbol
   get() = referenceFunctions(FqName("io.kotest.engine.execution.executeSpec")).single {
      val parameters = it.owner.valueParameters
      parameters.size == 1
   }

/**
 * Registers irCalls that execute each spec in turn.
 */
fun <T : IrElement> IrStatementsBuilder<T>.invokeSpec(pluginContext: IrPluginContext, spec: IrClass) {
   val callExecuteSpecFn = irCall(pluginContext.executeSpecFn)
   callExecuteSpecFn.putValueArgument(0, irCall(spec.constructors.first()))
   +callExecuteSpecFn
}

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
