package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.permissions.allowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.types.symbol
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
            val kaType = ref.type
            val superTypes = (kaType.allSupertypes(false) + kaType).toList()
            superTypes.mapNotNull {
               val classId = it.symbol?.classId?.takeIf { id -> id != StandardClassIds.Any }
               classId?.asSingleFqName()
            }
         }
      }
}
