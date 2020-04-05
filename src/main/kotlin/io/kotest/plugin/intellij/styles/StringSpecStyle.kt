package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object StringSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.StringSpec")

   override fun specStyleName(): String = "StringSpec"

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   /**
    * Matches tests of the form:
    *
    *   "some test" {}
    */
   private fun KtCallExpression.tryTest(): String? =
      this.extractStringFromStringInvokeWithLambda()

   /**
    * Matches tests of the form:
    *
    *   "some test".config(...) {}
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig(): String? =
      this.extractStringForStringExtensionFunctonWithRhsFinalLambda("config")

   /**
    * For a StringSpec we consider the following scenarios:
    *
    * "test name" { }
    * "test name".config(...) {}
    */
   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null

      return when (element) {
         is KtCallExpression -> element.tryTest()
         is KtDotQualifiedExpression -> element.tryTestWithConfig()
         else -> null
      }
   }

   /**
    * For a StringSpec we consider the following scenarios:
    *
    * "test name" { }
    * "test name".config(...) {}
    */
   override fun testPath(element: LeafPsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val ktcall = element.ifCallExpressionLhsStringOpenQuote()
      if (ktcall != null) return testPath(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return testPath(ktdot)

      return null
   }
}
