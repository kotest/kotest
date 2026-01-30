@file:OptIn(KaImplementationDetail::class)

package io.kotest.plugin.intellij.psi

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.psi.KtClassOrObject

object ElementUtils {

   private val logger = logger<ElementUtils>()

   /**
    * Given a [PsiElement] returns the [TestContext] that maps to the location of that element,
    * or returns null if the given element does not map to a Kotest spec or test.
    */
   fun findTestContext(element: PsiElement): TestContext? {
      return AnalysisUtils.withEdtSafeAnalysis {
         element.enclosingSpec()?.let { spec ->
            val test = SpecStyle.findTest(element)
            TestContext(spec, test).also {
               logger.info("Test context for $element is $it")
            }
         }
      }
   }
}

data class TestContext(val spec: KtClassOrObject, val test: Test?)
