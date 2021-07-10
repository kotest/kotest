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
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.dump
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

            val executeSpecFn: IrSimpleFunctionSymbol =
               pluginContext.referenceFunctions(FqName("io.kotest.core.js.executeSpec2")).single {
                  val parameters = it.owner.valueParameters
                  parameters.size == 1
               }

//            val executeKotestSpecFn = pluginContext.irFactory.buildFun {
//               name = Name.identifier("executeKotestSpec")
//               visibility = DescriptorVisibilities.INTERNAL
//               returnType = pluginContext.irBuiltIns.unitType
//            }.apply {
//               body = DeclarationIrBuilder(pluginContext, symbol).irBlockBody {
//                  val callPrintln = irCall(executeSpecFn)
//                  callPrintln.putValueArgument(0, irString("Executing spec=" + declaration.fqName.asString()))
//                  +callPrintln
//               }
//            }

            val registerKotest = pluginContext.irFactory.buildProperty {
               name = Name.identifier("kotestSpecEntryPoint")
            }.apply {
               parent = declaration
               backingField = pluginContext.irFactory.buildField {
                  type = pluginContext.irBuiltIns.unitType
                  isFinal = true
                  isExternal = false
                  isStatic = true // top level must be static
                  name = Name.identifier("kotestSpecEntryPoint")
               }.also {
                  it.correspondingPropertySymbol = this@apply.symbol
                  it.initializer = pluginContext.irFactory.createExpressionBody(startOffset, endOffset) {
                     this.expression = DeclarationIrBuilder(
                        pluginContext,
                        it.symbol
                     ).irBlock {
                        val callExecuteSpecFn = irCall(executeSpecFn)
                        callExecuteSpecFn.putValueArgument(0, irString("mytest"))
                        +callExecuteSpecFn
                     }
                  }
               }

               addGetter {
                  returnType = pluginContext.irBuiltIns.unitType
               }.also { func ->
                  func.body = DeclarationIrBuilder(pluginContext, func.symbol).irBlockBody {
                     val callExecuteSpecFn = irCall(executeSpecFn)
                     callExecuteSpecFn.putValueArgument(0, irString("mytest"))
                     +callExecuteSpecFn
//                     +irReturnFalse()
                  }
               }
            }

//            val obj = pluginContext.irFactory.buildClass {
//               this.modality = Modality.FINAL
//               this.kind = ClassKind.OBJECT
//               this.name = Name.identifier("_register_kotest_spec")
//            }.apply {
//               createImplicitParameterDeclarationWithWrappedDescriptor()
//               addConstructor {
//                  isPrimary = true
//               }.also { constructor ->
//                  constructor.body = DeclarationIrBuilder(pluginContext, constructor.symbol).irBlockBody {
//                     val callPrintln = irCall(funPrintln)
//                     callPrintln.putValueArgument(0, irString("qweqwewqe qwe qe qe qwe qwHello, World!"))
//                     +callPrintln
//                  }
//               }
//            }
//
//            obj.superTypes = listOf(pluginContext.irBuiltIns.anyType)
//            obj.thisReceiver = pluginContext.irFactory.createValueParameter(
//               startOffset = 0,
//               endOffset = 0,
//               origin = IrDeclarationOrigin.INSTANCE_RECEIVER,
//               symbol = IrValueParameterSymbolImpl(null),
//               name = Name.special("<this>"),
//               index = 0,
//               type = obj.rawType(),
//               varargElementType = null,
//               isCrossinline = false,
//               isNoinline = false,
//               isHidden = false,
//               isAssignable = false
//            )

//            messageCollector.report(CompilerMessageSeverity.WARNING, "declaration=$declaration")
//            messageCollector.report(CompilerMessageSeverity.WARNING, "kotlinFqName=${declaration.name}")

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

//            val registerSpecsVal = pluginContext.irFactory.buildProperty {
//               name = Name.identifier("boo")
//               visibility = DescriptorVisibilities.PRIVATE
//               modality = Modality.FINAL
//               isVar = false
//            }
//
//            registerSpecsVal.backingField = pluginContext.irFactory.buildField {
//               this.name = Name.identifier("boo")
//               this.type = pluginContext.irBuiltIns.booleanType
//               this.origin = IrDeclarationOrigin.PROPERTY_BACKING_FIELD
//               this.visibility = DescriptorVisibilities.PRIVATE
//               this.isStatic = true
//               this.isFinal = true
//            }
//
//            registerSpecsVal.backingField?.initializer = pluginContext.irFactory.createExpressionBody(
//               DeclarationIrBuilder(pluginContext, registerSpecsVal.symbol).irTrue()
//            )
//            registerSpecsVal.backingField!!.initializer =
//               DeclarationIrBuilder(pluginContext, registerSpecsVal.symbol).irExprBody(
//                  DeclarationIrBuilder(
//                     pluginContext,
//                     registerSpecsVal.symbol
//                  ).irReturnTrue()
//               )
//
//            registerSpecsVal.getter = pluginContext.irFactory.buildFun {
//               name = Name.identifier("<get-boo>")
//               visibility = DescriptorVisibilities.PRIVATE
//               modality = Modality.FINAL
//               origin = IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR
//               returnType = pluginContext.irBuiltIns.booleanType
//            }
//            registerSpecsVal.getter!!.correspondingPropertySymbol = registerSpecsVal.symbol
//            registerSpecsVal.getter!!.body =
//               DeclarationIrBuilder(pluginContext, registerSpecsVal.getter!!.symbol).irBlockBody {
////               val callPrintln = irCall(funPrintln)
////               callPrintln.putValueArgument(0, irString("qweqwewqe qwe qe qe qwe qwHello, World!"))
////               +callPrintln
//                  val r = irReturn(irGetField(null, registerSpecsVal.backingField!!))
//                  r.type = pluginContext.irBuiltIns.booleanType
//
//                  messageCollector.report(
//                     CompilerMessageSeverity.WARNING,
//                     "returnTargetSymbol=" + r.returnTargetSymbol
//                  )
//
//                  messageCollector.report(
//                     CompilerMessageSeverity.WARNING,
//                     "type=" + (r.type as IrType).classFqName
//                  )
//
//                  +r
//               }

//            messageCollector.report(
//               CompilerMessageSeverity.WARNING,
//               "reg=" + registerSpecsVal.dump()
//            )
//

            declaration.declarations.find { it.nameForIrSerialization.asString() == "Foo" }?.apply {

               val foo = this as IrProperty

               messageCollector.report(CompilerMessageSeverity.WARNING, foo.toString())
               messageCollector.report(
                  CompilerMessageSeverity.WARNING,
                  "Foo=" + foo.dump()
               )

               messageCollector.report(CompilerMessageSeverity.WARNING, registerKotest.toString())
               messageCollector.report(
                  CompilerMessageSeverity.WARNING,
                  "registerKotest=" + registerKotest.dump()
               )

            }

            declaration.addChild(registerKotest)


//               val foo = this as IrProperty

//
//               val body = foo.getter!!.body!! as IrBlockBody
//               val r = body.statements.first() as IrReturn
//               val v = r.value as IrGetField
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "r=" + r.dump()
//               )
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "returnTargetSymbol=" + r.returnTargetSymbol
//               )
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "returnTargetSymbol=" + (r.returnTargetSymbol as IrSimpleFunctionSymbol)
//               )
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "v.origin=" + v.origin
//               )
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "v.type=" + (v.type as IrType).classFqName
//               )
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "attributeOwnerId=" + v.attributeOwnerId
//               )
//
//               messageCollector.report(
//                  CompilerMessageSeverity.WARNING,
//                  "foo_parent=" + foo.parent::class.java
//               )
//            }
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
//            declaration.addChild(registerSpecsVal)
//            messageCollector.report(
//               CompilerMessageSeverity.WARNING,
//               "declaration=" + declaration.dump()
//            )

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
