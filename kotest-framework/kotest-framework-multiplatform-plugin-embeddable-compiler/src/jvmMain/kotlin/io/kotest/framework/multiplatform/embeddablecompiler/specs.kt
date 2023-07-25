package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.isClass
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.name.FqName

val specClasses = listOf(
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

val abstractProjectConfigFqName = FqName("io.kotest.core.config.AbstractProjectConfig")

/**
 * Returns any specs declared at the top level in this file.
 */
fun IrFile.specs() = declarations.filterIsInstance<IrClass>().filter { it.isSpecClass() }

/**
 * Returns true if this IrClass is an instantiable project config
 */
fun IrClass.isInstantiableProjectConfig() =
   kind.isClass && modality != Modality.ABSTRACT && modality != Modality.SEALED &&
      superTypes().any { it.classFqName == abstractProjectConfigFqName }

/**
 * Recursively returns all supertypes for an [IrClass] to the top of the type tree.
 */
fun IrClass.superTypes(): List<IrType> =
   this.superTypes + this.superTypes.flatMap { it.getClass()?.superTypes() ?: emptyList() }

/**
 * Returns true if any of the parents of this class are a spec class.
 */
fun IrClass.isSpecClass() =
   superTypes().mapNotNull { it.classFqName?.asString() }.intersect(specClasses).isNotEmpty()
