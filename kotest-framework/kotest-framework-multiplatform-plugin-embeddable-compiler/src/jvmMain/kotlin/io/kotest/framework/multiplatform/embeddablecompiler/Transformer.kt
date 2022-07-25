package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addChild
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.toLogger
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import java.util.concurrent.CopyOnWriteArrayList

abstract class Transformer(protected val messageCollector: MessageCollector, protected val pluginContext: IrPluginContext) : IrElementTransformerVoidWithContext() {
   private val specs = CopyOnWriteArrayList<IrClass>()
   private var configs = CopyOnWriteArrayList<IrClass>()

   override fun visitClassNew(declaration: IrClass): IrStatement {
      super.visitClassNew(declaration)
      if (declaration.isProjectConfig()) configs.add(declaration)
      return declaration
   }

   override fun visitFileNew(declaration: IrFile): IrFile {
      super.visitFileNew(declaration)
      val specs = declaration.specs()
      messageCollector.toLogger().log("${declaration.name} contains ${specs.size} spec(s): ${specs.joinToString(", ") { it.kotlinFqName.asString() }}")
      this.specs.addAll(specs)
      return declaration
   }

   override fun visitModuleFragment(declaration: IrModuleFragment): IrModuleFragment {
      val fragment = super.visitModuleFragment(declaration)

      messageCollector.toLogger().log("Detected ${configs.size} configs:")
      configs.forEach {
         messageCollector.toLogger().log(it.kotlinFqName.asString())
      }

      messageCollector.toLogger().log("Detected ${specs.size} JS specs:")
      specs.forEach {
         messageCollector.toLogger().log(it.kotlinFqName.asString())
      }

      if (specs.isEmpty()) {
         return fragment
      }

      val file = declaration.files.first()
      val launcher = generateLauncher(specs, configs, file)
      file.addChild(launcher)

      return fragment
   }

   abstract fun generateLauncher(specs: Iterable<IrClass>, configs: Iterable<IrClass>, declarationParent: IrDeclarationParent): IrDeclaration

   protected val launcherClass = pluginContext.referenceClass(FqName(EntryPoint.TestEngineClassName))
      ?: error("Cannot find ${EntryPoint.TestEngineClassName} class reference")

   protected val launcherConstructor = launcherClass.constructors.first { it.owner.valueParameters.isEmpty() }

   protected val withSpecsFn = launcherClass.getSimpleFunction(EntryPoint.WithSpecsMethodName)
      ?: error("Cannot find function ${EntryPoint.WithSpecsMethodName}")

   protected val withConfigFn = launcherClass.getSimpleFunction(EntryPoint.WithConfigMethodName)
      ?: error("Cannot find function ${EntryPoint.WithConfigMethodName}")
}
