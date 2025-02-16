package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Recursively returns the list of classes and interfaces extended or implemented by the class.
 */
fun KtClassOrObject.getAllSuperClasses(): List<FqName> {
   return superTypeListEntries.mapNotNull { it.typeReference }
      .flatMap { ref ->
         analyze(this) {
            val kaType: KaType = ref.type
            val superTypes: List<KaType> = (kaType.allSupertypes(shouldApproximate = false) + kaType).toList()
            superTypes.mapNotNull {
               // don't include the Any supertype that is the root of all types
               val classId: ClassId? = it.symbol?.classId?.takeIf { id -> id != StandardClassIds.Any }
               classId?.asSingleFqName()
            }
         }
      }
}
