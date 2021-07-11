package io.kotest.framework.js

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.Name

class SpecIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {
   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      messageCollector.report(CompilerMessageSeverity.WARNING, "qweqweqe" + moduleFragment)

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         override fun visitFileNew(declaration: IrFile): IrFile {
            messageCollector.report(CompilerMessageSeverity.WARNING, declaration.name)
            declaration.specs().forEach { spec ->

               // we use a public val to register each spec, so this name must be unique across all files
               // therefore using FQN spec name seems a safe bet
               val entryPointPropertyName = "kotestSpecEntryPoint_${spec.kotlinFqName.asString().replace(".", "_")}"
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "entryPointPropertyName=$entryPointPropertyName"
//               )

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
