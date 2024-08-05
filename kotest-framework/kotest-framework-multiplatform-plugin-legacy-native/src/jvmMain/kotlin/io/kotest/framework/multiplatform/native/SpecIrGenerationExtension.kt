package io.kotest.framework.multiplatform.native

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.toLogger
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrSingleStatementBuilder
import org.jetbrains.kotlin.ir.builders.Scope
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
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class SpecIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {

         val specs = CopyOnWriteArrayList<IrClass>()

         override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
            val fragment = super.visitModuleFragment(declaration)
            messageCollector.toLogger().log("Detected ${specs.size} native specs:")
            specs.forEach {
               messageCollector.toLogger().log(it.kotlinFqName.asString())
            }
            if (specs.isEmpty()) return fragment

            val file: IrFile = declaration.files.first()

            val launcherClass = pluginContext.referenceClass(ClassId.fromString(EntryPoint.TestEngineClassName))
               ?: error("Cannot find ${EntryPoint.TestEngineClassName} class reference")

            val launcherConstructor = launcherClass.constructors.first { it.owner.valueParameters.isEmpty() }

            val launchFn = launcherClass.getSimpleFunction(EntryPoint.LaunchMethodName)
               ?: error("Cannot find function ${EntryPoint.LaunchMethodName}")

            val withSpecsFn = launcherClass.getSimpleFunction(EntryPoint.WithSpecsMethodName)
               ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")

            val withTeamCityListenerMethodNameFn =
               launcherClass.getSimpleFunction(EntryPoint.WithTeamCityListenerMethodName)
                  ?: error("Cannot find function ${EntryPoint.WithTeamCityListenerMethodName}")

            val eagerAnnotationName = ClassId.fromString("kotlin/native/EagerInitialization")
            val eagerAnnotation = pluginContext.referenceClass(eagerAnnotationName)
               ?: error("Cannot find eager initialisation annotation class $eagerAnnotationName")

            val eagerAnnotationConstructor = eagerAnnotation.constructors.single()

            val launcher = pluginContext.irFactory.buildProperty {
               name = Name.identifier(EntryPoint.LauncherValName)
            }.apply {
               parent = file
               annotations += IrSingleStatementBuilder(pluginContext, Scope(this.symbol), UNDEFINED_OFFSET, UNDEFINED_OFFSET).build { irCall(eagerAnnotationConstructor) }

               backingField = pluginContext.irFactory.buildField {
                  type = pluginContext.irBuiltIns.unitType
                  isFinal = true
                  isExternal = false
                  isStatic = true // top level vals must be static
                  name = Name.identifier(EntryPoint.LauncherValName)
               }.also { field ->
                  field.correspondingPropertySymbol = this@apply.symbol
                  field.initializer = pluginContext.irFactory.createExpressionBody(
                     startOffset,
                     endOffset,
                     DeclarationIrBuilder(pluginContext, field.symbol).irBlock {
                        +irCall(launchFn).also { launch ->
                           launch.dispatchReceiver = irCall(withTeamCityListenerMethodNameFn).also { withTeamCity ->
                              withTeamCity.dispatchReceiver = irCall(withSpecsFn).also { withSpecs ->
                                 withSpecs.dispatchReceiver = irCall(launcherConstructor)
                                 withSpecs.putValueArgument(
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
                  )
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
            super.visitFileNew(declaration)
            val specs = declaration.specs()
            messageCollector.toLogger().log("${declaration.name} contains ${specs.size} spec(s): ${specs.joinToString(", ") { it.kotlinFqName.asString() }}")
            this.specs.addAll(specs)
            return declaration
         }

      }, null)
   }
}

// These extension properties are available in org.jetbrains.kotlin.ir.declarations, but were moved from one file to
// another in Kotlin 1.7. This breaks backwards compatibility with earlier versions of Kotlin.
// So instead of using the provided implementations, we've copied them here, so we can work with both Kotlin 1.7+ and earlier
// versions without issue.
// See https://github.com/kotest/kotest/issues/3060 and https://youtrack.jetbrains.com/issue/KT-52888 for more information.
private val IrFile.path: String get() = fileEntry.name
private val IrFile.name: String get() = File(path).name
