package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

val specBuilders = listOf(
   "behaviorSpec",
   "describeSpec",
   "expectSpec",
   "featureSpec",
   "freeSpec",
   "funSpec",
   "shouldSpec",
   "stringSpec",
   "wordSpec"
)

/**
 * Returns true if this function is a factory definition in the form:
 *
 * fun myfactory(...) = stringSpec { }
 */
fun KtNamedFunction.getFactoryName(): String? {
   return when (val call = this.bodyExpression) {
      is KtCallExpression -> {
         val specBuilder = call.getChildOfType<KtNameReferenceExpression>()?.text
         if (specBuilders.contains(specBuilder)) this.name else null
      }
      else -> null
   }
}
