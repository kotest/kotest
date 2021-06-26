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
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.buildField
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.declarations.buildProperty
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTrue
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.isTopLevelDeclaration
import org.jetbrains.kotlin.ir.util.nameForIrSerialization
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addIfNotNull

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
      val specs = mutableListOf<String>()
      moduleFragment.accept(SpecVisitor(specs), null)
      messageCollector.report(CompilerMessageSeverity.WARNING, "specs=$specs")

      val funPrintln: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
         .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].type == pluginContext.irBuiltIns.anyNType
         }

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {
         override fun visitFileNew(declaration: IrFile): IrFile {
            messageCollector.report(CompilerMessageSeverity.WARNING, "declaration=$declaration")
            messageCollector.report(CompilerMessageSeverity.WARNING, "kotlinFqName=${declaration.name}")

//            val registerSpecsFunction: IrSimpleFunction = pluginContext.irFactory.buildFun {
//               name = Name.identifier("registerSpecs")
//               returnType = pluginContext.irBuiltIns.unitType
//               visibility = DescriptorVisibilities.PRIVATE
//            }
//
//            registerSpecsFunction.body = DeclarationIrBuilder(pluginContext, registerSpecsFunction.symbol).irBlockBody {
//               val callPrintln = irCall(funPrintln)
//               callPrintln.putValueArgument(0, irString("qweqwewqe qwe qe qe qwe qwHello, World!"))
//               +callPrintln
//            }

            val registerSpecsVal = pluginContext.irFactory.buildProperty {
               name = Name.identifier("registerSpecs")
               visibility = DescriptorVisibilities.PRIVATE
               modality = Modality.FINAL
               isVar = false
            }
            registerSpecsVal.backingField = pluginContext.irFactory.buildField {
               this.name = Name.identifier("registerSpecs" )
               this.type = pluginContext.irBuiltIns.booleanType
               this.origin = IrDeclarationOrigin.PROPERTY_BACKING_FIELD
               this.visibility = DescriptorVisibilities.PRIVATE
               this.isStatic = true
               this.isFinal = true
            }
            registerSpecsVal.backingField?.initializer = pluginContext.irFactory.createExpressionBody(
               DeclarationIrBuilder(pluginContext, declaration.symbol).irTrue()
            )
            registerSpecsVal.getter = pluginContext.irFactory.buildFun {
               this.name = Name.identifier("<get-registerSpecs>")
               this.visibility = DescriptorVisibilities.PUBLIC
               modality = Modality.FINAL
               this.origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
               this.returnType = pluginContext.irBuiltIns.booleanType
            }
            registerSpecsVal.getter!!.correspondingPropertySymbol = registerSpecsVal.symbol
            registerSpecsVal.getter!!.body =
               DeclarationIrBuilder(pluginContext, registerSpecsVal.getter!!.symbol).irBlockBody {
//               val callPrintln = irCall(funPrintln)
//               callPrintln.putValueArgument(0, irString("qweqwewqe qwe qe qe qwe qwHello, World!"))
//               +callPrintln
                  +irReturn(irGetField(null, registerSpecsVal.backingField!!))
               }

            messageCollector.report(
               CompilerMessageSeverity.WARNING,
               "reg=" + registerSpecsVal.dump()
            )

            declaration.declarations.find { it.nameForIrSerialization.asString() == "foo" }?.apply {
               val p = this as IrProperty
               val backing = p.backingField!!
               val init = backing.initializer!!
               messageCollector.report(CompilerMessageSeverity.WARNING, p.name.toString())
               messageCollector.report(
                  CompilerMessageSeverity.WARNING,
                  "foo=" + p.dump()
               )
            }
////
//            declaration.declarations.find { it.nameForIrSerialization.asString() == "foo" }?.apply {
//               val p = this as IrClass
//               messageCollector.report(CompilerMessageSeverity.WARNING, p.name.toString())
//               messageCollector.report(CompilerMessageSeverity.WARNING, p.symbol.toString())
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  p.declarations.find { it is IrConstructor }!!.nameForIrSerialization.toString()
//               )
//            }

//            messageCollector.report(CompilerMessageSeverity.WARNING, obj.name.toString())
//            messageCollector.report(CompilerMessageSeverity.WARNING, obj.symbol.toString())

//            registerSpecsVal.backingField!!.initializer = pluginContext.irFactory.createExpressionBody(
//               DeclarationIrBuilder(
//                  pluginContext,
//                  registerSpecsVal.backingField!!.symbol
//               ).irNull()
//            )
//            declaration.addChild(registerSpecsFunction)
            declaration.addChild(registerSpecsVal)
            messageCollector.report(
               CompilerMessageSeverity.WARNING,
               "declaration=" + declaration.dump()
            )

            return super.visitFileNew(declaration)
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

/**
 * An IR visitor that acts on instances of [IrClass] that are subtypes of Kotest specs.
 */
class SpecVisitor(
   private val specs: MutableList<String>
) : IrElementVisitor<Unit, Nothing?> {
   override fun visitElement(element: IrElement, data: Nothing?) {
      if (element is IrClass) {
         if (element.isSpecClass()) {
            specs.addIfNotNull(element.classId?.asString())
         }
      } else {
         element.acceptChildren(this, null)
      }
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
