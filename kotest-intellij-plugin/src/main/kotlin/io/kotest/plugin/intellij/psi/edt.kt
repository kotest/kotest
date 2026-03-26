package io.kotest.plugin.intellij.psi

import com.intellij.openapi.diagnostic.logger
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.permissions.KaAnalysisPermissionRegistry

internal object AnalysisUtils {

   private val logger = logger<AnalysisUtils>()

   @OptIn(KaImplementationDetail::class)
   fun <T> withEdtSafeAnalysis(thunk: () -> T): T? {
      return if (KaAnalysisPermissionRegistry.getInstance().isAnalysisAllowedOnEdt) {
         thunk()
      } else {
         try {
            KaAnalysisPermissionRegistry.getInstance().isAnalysisAllowedOnEdt = true
            thunk()
         } catch (e: Throwable) {
            logger.warn("Failed to execute thunk in analysis mode", e)
            return null
         } finally {
            KaAnalysisPermissionRegistry.getInstance().isAnalysisAllowedOnEdt = false
         }
      }
   }

}
