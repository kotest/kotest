package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createTopLevelFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.ConeDynamicType
import org.jetbrains.kotlin.fir.types.create
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irNull
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(UnsafeDuringIrConstructionAPI::class)
abstract class Transformer(
   private val messageCollector: MessageCollector,
   protected val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

   private val specs = CopyOnWriteArrayList<IrClass>()
   private var configs = CopyOnWriteArrayList<IrClass>()
   private var configFile: IrFile? = null

   override fun visitClassNew(declaration: IrClass): IrStatement {
      super.visitClassNew(declaration)
      if (declaration.isInstantiableProjectConfig()) configs.add(declaration)
      return declaration
   }

   override fun visitFunctionNew(declaration: IrFunction): IrStatement {
      if (declaration.name == Name.identifier("config") && declaration.getPackageFragment().packageFqName.asString() == "io.kotest.js") {
         configFile = declaration.file
      }
      return super.visitFunctionNew(declaration)
   }

   override fun visitFileNew(declaration: IrFile): IrFile {
      super.visitFileNew(declaration)
      val specs = declaration.specs()
      messageCollector.report(
         CompilerMessageSeverity.INFO,
         "${declaration.name} contains ${specs.size} spec(s): ${specs.joinToString(", ") { it.kotlinFqName.asString() }}"
      )
      this.specs.addAll(specs)
      return declaration
   }

   override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
      val fragment: IrModuleFragment = super.visitModuleFragment(declaration)

      messageCollector.report(CompilerMessageSeverity.INFO, "Detected ${configs.size} configs:")
      configs.forEach {
         messageCollector.report(CompilerMessageSeverity.INFO, it.kotlinFqName.asString())
      }

      messageCollector.report(CompilerMessageSeverity.INFO, "Detected ${specs.size} JS specs:")
      specs.forEach {
         messageCollector.report(CompilerMessageSeverity.INFO, it.kotlinFqName.asString())
      }

      if (specs.isEmpty()) {
         return fragment
      }

      val file = configFile ?: error("config function was not declared in io.kotest.js")
      val launcher = generateLauncher(specs, configs, file)
      file.addChild(launcher)

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
      testFilterArg: IrValueParameter?,
      constructorGenerator: IrBuilderWithScope.() -> IrExpression
   ): IrCall {
      return irCall(launchFunction).also { promise: IrCall ->
         promise.dispatchReceiver = irCall(withSpecsFn).also { withSpecs ->
            withSpecs.putValueArgument(
               0,
               irVararg(
                  pluginContext.irBuiltIns.stringType,
                  specs.map { irCall(it.constructors.first()) }
               )
            )
            withSpecs.dispatchReceiver = irCall(withPlatformFn).also { withPlatform ->
               withPlatform.dispatchReceiver = irCall(withBasicConsoleListenerFn).also { withBasicConsoleListener ->
                  withBasicConsoleListener.dispatchReceiver = irCall(withTestFilterFn).also { withTestFilter ->
                     withTestFilter.putValueArgument(
                        0,
                        if (testFilterArg == null) irNull() else irGet(testFilterArg),
                     )
                     withTestFilter.dispatchReceiver = irCall(withConfigFn).also { withConfig ->
                        withConfig.putValueArgument(
                           0,
                           irVararg(
                              pluginContext.irBuiltIns.stringType,
                              configs.map { irCall(it.constructors.first()) }
                           )
                        )
                        withConfig.dispatchReceiver = constructorGenerator()
                     }
                  }
               }
            }
         }
      }
   }

   protected val launcherClass by lazy {
      pluginContext.referenceClass(ClassId.fromString(EntryPoint.TestEngineClassName))
         ?: error("Cannot find ${EntryPoint.TestEngineClassName} class reference")
   }

   protected val launcherConstructor: IrConstructorSymbol by lazy { launcherClass.constructors.first { it.owner.valueParameters.isEmpty() } }

   protected abstract val withPlatformMethodName: String

   private val withPlatformFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(withPlatformMethodName)
         ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")
   }

   private val withSpecsFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithSpecsMethodName)
         ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")
   }

   private val withConfigFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithConfigMethodName)
         ?: error("Cannot find function ${EntryPoint.WithConfigMethodName}")
   }

   private val withTestFilterFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithTestFilterFnName)
         ?: error("Cannot find function ${EntryPoint.WithTestFilterFnName}")
   }

   private val withTeamCityListenerFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithTeamCityListenerMethodName)
         ?: error("Cannot find function ${EntryPoint.WithTeamCityListenerMethodName}")
   }

   private val withBasicConsoleListenerFn: IrSimpleFunctionSymbol by lazy {
      launcherClass.getSimpleFunction(EntryPoint.WithBasicConsoleTestEngineListener)
         ?: error("Cannot find function ${EntryPoint.WithBasicConsoleTestEngineListener}")
   }
}

@OptIn(ExperimentalTopLevelDeclarationsGenerationApi::class)
class SimpleClassGenerator(
   session: FirSession,
   private val messageCollector: MessageCollector
) : FirDeclarationGenerationExtension(session) {

   companion object {
      val runKotestFn = CallableId(FqName("io.kotest.js"), Name.identifier("runKotest"))
   }

   override fun getTopLevelCallableIds(): Set<CallableId> {
      messageCollector.report(CompilerMessageSeverity.STRONG_WARNING, "getTopLevelCallableIds")
      return setOf(runKotestFn)
   }

   override fun generateFunctions(
      callableId: CallableId,
      context: MemberGenerationContext?
   ): List<FirNamedFunctionSymbol> {
      messageCollector.report(
         CompilerMessageSeverity.STRONG_WARNING,
         "generateFunctions " + callableId.asFqNameForDebugInfo()
      )
      return listOf(createTopLevelFunction(Key, runKotestFn, ConeDynamicType.create(session)).symbol)
   }

   override fun hasPackage(packageFqName: FqName): Boolean {
      messageCollector.report(CompilerMessageSeverity.STRONG_WARNING, "has package = " + packageFqName.asString())
      return packageFqName == FqName("io.kotest.js")
   }

   object Key : GeneratedDeclarationKey()
}

class ConfigClassRegistrar(private val messageCollector: MessageCollector) : FirExtensionRegistrar() {
   override fun ExtensionRegistrarContext.configurePlugin() {
      { session: FirSession -> SimpleClassGenerator(session, messageCollector) }.unaryPlus()
   }
}
