package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestPathEntry
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.extractStringForStringExtensionFunctonWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringFromStringInvokeWithLambda
import io.kotest.plugin.intellij.psi.extractStringLiteralFromLhsOfInfixFunction
import io.kotest.plugin.intellij.psi.ifBinaryExpressionOperationIdent
import io.kotest.plugin.intellij.psi.ifCallExpressionLhsStringOpenQuote
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FreeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FreeSpec")

   override fun specStyleName(): String = "Free Spec"

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParentContainers(element: PsiElement): List<Test> {
      // if parent is null then we have hit the end
      val p = element.parent ?: return emptyList()
      val context = if (p is KtBinaryExpression) listOfNotNull(p.tryContainer()) else emptyList()
      return locateParentContainers(p) + context
   }

   /**
    * A test of the form:
    *
    *   "test name"{ }
    *
    */
   private fun KtCallExpression.tryTest(): Test? {
      val string = extractStringFromStringInvokeWithLambda() ?: return null
      return buildTest(TestName(string.text, string.interpolated), this, TestType.Test)
   }

   /**
    * Matches tests of the form:
    *
    *   "some test".config(...) {}
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig(): Test? {
      val string = extractStringForStringExtensionFunctonWithRhsFinalLambda("config") ?: return null
      return buildTest(TestName(string.text, string.interpolated), this, TestType.Test)
   }

   /**
    * Matches tests of the form:
    *
    *   "some test" - {}
    */
   private fun KtBinaryExpression.tryContainer(): Test? {
      val string = extractStringLiteralFromLhsOfInfixFunction(listOf("-")) ?: return null
      return buildTest(TestName(string.text, string.interpolated), this, TestType.Container)
   }

   private fun buildTest(testName: TestName, element: PsiElement, type: TestType): Test {
      val contexts = locateParentContainers(element)
      val path = (contexts.map { it.name } + testName)
      return Test(testName, path.map { TestPathEntry(it.name) }, type, false, path.size == 1, element)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryTest()
         is KtDotQualifiedExpression -> element.tryTestWithConfig()
         is KtBinaryExpression -> element.tryContainer()
         else -> null
      }
   }

   /**
    * For a FreeSpec we consider the following scenarios:
    *
    * "test name" {} // a test
    * "test name" - {} // a container
    */
   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLhsStringOpenQuote()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      val ktbinary = element.ifBinaryExpressionOperationIdent()
      if (ktbinary != null) return test(ktbinary)

      return null
   }
}
