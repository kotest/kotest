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
@OptIn(KaAllowAnalysisOnEdt::class)
fun KtClassOrObject.getAllSuperClasses(): List<FqName> {
   return superTypeListEntries.mapNotNull { it.typeReference }
      .flatMap { ref ->
         // SurroundSelectionWithFunctionIntention.isAvailable is called in EDT before the intention is applied
         // unfortunately API to avoid this was introduced in 23.2 only
         // this we need to move intentions to the facade or accept EDT here until 23.2- are still supported
         allowAnalysisOnEdt {
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
}
