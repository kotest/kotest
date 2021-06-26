package io.kotest.framework.js

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
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
