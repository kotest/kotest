@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.toLogger
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.name.ClassId
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

abstract class Transformer(
   private val messageCollector: MessageCollector,
   protected val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

   private val specs = CopyOnWriteArrayList<IrClass>()
   private var configs = CopyOnWriteArrayList<IrClass>()

   override fun visitClassNew(declaration: IrClass): IrStatement {
      super.visitClassNew(declaration)
      if (declaration.isInstantiableProjectConfig()) configs.add(declaration)
      return declaration
   }

   // todo only run this for files in the test source sets
   override fun visitFileNew(declaration: IrFile): IrFile {
      super.visitFileNew(declaration)
      val specs = declaration.specs()
      messageCollector.toLogger()
         .warning("${declaration.name} contains ${specs.size} spec(s): ${specs.joinToString(", ") { it.kotlinFqName.asString() }}")
      this.specs.addAll(specs)
      return declaration
   }

   @OptIn(ObsoleteDescriptorBasedAPI::class)
   override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
      val fragment = super.visitModuleFragment(declaration)
      if (declaration.files.isEmpty()) return fragment

      messageCollector.toLogger().warning("Detected ${configs.size} configs:")
      configs.forEach {
         messageCollector.toLogger().warning("Config name: " + it.kotlinFqName.asString())
      }

      messageCollector.toLogger().warning("Detected ${specs.size} JS specs:")
      specs.forEach {
         messageCollector.toLogger().warning("Spec: " + it.kotlinFqName.asString())
      }

      if (specs.isEmpty()) {
         return fragment
      }

      // we want to write our launcher function to a well known package name, so the gradle plugin can execute it
      // so we can take any file, and strip out any package paths to get the base src path
      // we are making an assumption the build folder contains jsTest or wasmJsTest
      val outputDir = File(declaration.files.first().path.substringBefore("jsTest") + "jsTest/kotlin")
      messageCollector.toLogger().warning("outputDir: $outputDir")

      //account for classes in root package. Those need to be imported too!
      val imports= specs.filter { it.packageFqName==null || !it.kotlinFqName.asString().contains(".") }.map { "import `${it.kotlinFqName.asString()}`"}.joinToString("\n")

      val specs = specs.joinToString(",") { it.kotlinFqName.asString().split(".").map { "`$it`" }.joinToString(".") + "()" }
      val configs = if (configs.isEmpty()) "" else ".withProjectConfig(${configs.first().kotlinFqName.asString()}())"

      // todo move this to a generated file not a source written file
      // requires an answer to this https://discuss.kotlinlang.org/t/create-new-file-using-compiler-plugin/30225
      if(!outputDir.exists())
      if(!outputDir.mkdirs()) throw RuntimeException("Cannot create output dir $outputDir")
      if(!outputDir.isDirectory) throw RuntimeException("$outputDir is not a directory")
      val myFile = File(outputDir, "runKotest.kt")
      myFile.writeText(
         """
package io.kotest.runtime.js

import io.kotest.engine.TestEngineLauncher
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

$imports

@OptIn(ExperimentalJsExport::class)
@JsExport
fun runKotest(type: String) {
   val launcher = TestEngineLauncher()
   .withJs()
   .withSpecs($specs)
   $configs
   if (type === "TeamCity") launcher.withTeamCityListener().promise() else launcher.promise()
}
""".trim()
      )

      return fragment
   }

   abstract fun generateLauncher(
      specs: Iterable<IrClass>,
      configs: Iterable<IrClass>,
      declarationParent: IrDeclarationParent
   ): IrDeclaration

   protected fun IrBuilderWithScope.callLauncher(
      launchFunction: IrSimpleFunctionSymbol,
      specs: Iterable<IrClass>,
      configs: Iterable<IrClass>,
      constructorGenerator: IrBuilderWithScope.() -> IrExpression
   ): IrCall {
      return irCall(launchFunction).also { promise: IrCall ->
         promise.dispatchReceiver = irCall(withSpecsFn).also { withSpecs ->
            withSpecs.arguments[0] =
               irVararg(pluginContext.irBuiltIns.stringType, specs.map { irCall(it.constructors.first()) })
            withSpecs.dispatchReceiver = irCall(withPlatformFn).also { withPlatform ->
               val config = configs.firstOrNull()
               if (config != null) {
                  withPlatform.dispatchReceiver = irCall(withProjectConfigFn).also { withConfig ->
                     withConfig.arguments[0] = irCall(config.constructors.first())
                     withConfig.dispatchReceiver = constructorGenerator()
                  }
               } else {
                  withPlatform.dispatchReceiver = constructorGenerator()
               }
            }
         }
      }
   }

   protected val launcherClass by lazy {
      pluginContext.referenceClass(ClassId.fromString(EntryPoint.TEST_ENGINE_CLASS_NAME))
         ?: error("Cannot find ${EntryPoint.TEST_ENGINE_CLASS_NAME} class reference")
   }

   protected val launcherConstructor by lazy {
      launcherClass.constructors.first { symbol ->
         val valueParameters =
            symbol.owner.parameters.filter { it.kind == IrParameterKind.Regular || it.kind == IrParameterKind.Context }
         valueParameters.isEmpty()
      }
   }

   protected abstract val withPlatformMethodName: String

   private val withPlatformFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(withPlatformMethodName)
         ?: error("Cannot find function ${EntryPoint.WITH_SPECS_FUNCTION_NAME}")
   }

   private val withSpecsFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WITH_SPECS_FUNCTION_NAME)
         ?: error("Cannot find function ${EntryPoint.WITH_SPECS_FUNCTION_NAME}")
   }

   private val withProjectConfigFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WITH_PROJECT_CONFIG_METHOD_NAME)
         ?: error("Cannot find function ${EntryPoint.WITH_PROJECT_CONFIG_METHOD_NAME}")
   }
}
