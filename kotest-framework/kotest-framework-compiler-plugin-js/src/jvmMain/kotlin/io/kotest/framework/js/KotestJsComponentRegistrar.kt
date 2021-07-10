package io.kotest.framework.js

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
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
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KotestJsComponentRegistrar : ComponentRegistrar {

   override fun registerProjectComponents(
      project: MockProject,
      configuration: CompilerConfiguration
   ) {

      val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      configuration.kotlinSourceRoots.forEach {
         messageCollector.report(
            CompilerMessageSeverity.WARNING,
            "*** Hello from ***" + it.path
         )
      }

      IrGenerationExtension.registerExtension(project, SpecIrGenerationExtension(messageCollector))

   }
}

class SpecIrGenerationExtension(
   private val messageCollector: MessageCollector,
) : IrGenerationExtension {
   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         override fun visitFileNew(declaration: IrFile): IrFile {
            messageCollector.report(CompilerMessageSeverity.WARNING, "file=${declaration.name}")
            val specs = declaration.declarations.filterIsInstance<IrClass>().filter { it.isSpecClass() }
            messageCollector.report(CompilerMessageSeverity.WARNING, "specs=${specs.map { it.name.asString() }}")

            if (specs.isNotEmpty()) {
               val registerKotest = pluginContext.irFactory.buildProperty {
                  name = Name.identifier("kotestSpecEntryPoint_${declaration.name.removeSuffix(".kt")}")
               }.apply {
                  parent = declaration
                  backingField = pluginContext.irFactory.buildField {
                     type = pluginContext.irBuiltIns.unitType
                     isFinal = true
                     isExternal = false
                     isStatic = true // top level must be static
                     name = Name.identifier("kotestSpecEntryPoint_${declaration.name.removeSuffix(".kt")}}")
                  }.also {
                     it.correspondingPropertySymbol = this@apply.symbol
                     it.initializer = pluginContext.irFactory.createExpressionBody(startOffset, endOffset) {
                        this.expression = DeclarationIrBuilder(pluginContext, it.symbol).irBlock {
                           invokeSpecs(pluginContext, specs)
                        }
                     }
                  }

                  addGetter {
                     returnType = pluginContext.irBuiltIns.unitType
                  }.also { func ->
                     func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                        invokeSpecs(pluginContext, specs)
                     }
                  }
               }

               declaration.addChild(registerKotest)
            }

            return declaration
         }
      }, null)
   }
}

private val specClasses = listOf(
   "io.kotest.core.spec.style.FunSpec",
   "io.kotest.core.spec.style.StringSpec",
   "io.kotest.core.spec.style.DescribeSpec",
   "io.kotest.core.spec.style.WordSpec",
   "io.kotest.core.spec.style.FreeSpec",
   "io.kotest.core.spec.style.ShouldSpec",
   "io.kotest.core.spec.style.FeatureSpec",
   "io.kotest.core.spec.style.ExpectSpec",
   "io.kotest.core.spec.style.BehaviorSpec",
)

val IrPluginContext.executeSpecFn: IrSimpleFunctionSymbol
   get() = referenceFunctions(FqName("io.kotest.core.js.executeSpec2")).single {
      val parameters = it.owner.valueParameters
      parameters.size == 1
   }

fun <T : IrElement> IrStatementsBuilder<T>.invokeSpecs(pluginContext: IrPluginContext, specs: List<IrClass>) {
   specs.forEach {
      val callExecuteSpecFn = irCall(pluginContext.executeSpecFn)
      callExecuteSpecFn.putValueArgument(0, irString(it.name.asString()))
      +callExecuteSpecFn
   }
}

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
