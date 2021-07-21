package io.kotest.framework.native

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrStatementsBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.name.FqName

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
fun IrFile.specs() = declarations.filterIsInstance<IrClass>().filter { it.isSpecClass() }

/**
 * Recursively returns all supertypes for an [IrClass] to the top of the type tree.
 */
fun IrClass.superTypes(): List<IrType> =
   this.superTypes + this.superTypes.flatMap { it.getClass()?.superTypes() ?: emptyList() }

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

/**
 * Returns true if any of the parents of this class are a spec class.
 */
fun IrClass.isSpecClass() =
   superTypes().mapNotNull { it.classFqName?.asString() }.intersect(specClasses).isNotEmpty()
