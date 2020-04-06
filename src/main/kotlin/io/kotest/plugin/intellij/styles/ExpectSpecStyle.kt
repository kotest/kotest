package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object ExpectSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.ExpectSpec")

   override fun specStyleName(): String = "ExpectSpec"

   override fun generateTest(specName: String, name: String): String {
      return "expect(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   private fun PsiElement.locateParentTests(): List<Test> {
      // if parent is null then we have hit the end
      val p = parent ?: return emptyList()
      val context = if (p is KtCallExpression) listOfNotNull(p.tryContext()) else emptyList()
      return parent.locateParentTests() + context
   }

   private fun KtCallExpression.tryContext(): Test? {
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(context, this)
   }

   private fun KtCallExpression.tryExpect(): Test? {
      val expect = extractStringArgForFunctionWithStringAndLambdaArgs("expect") ?: return null
      return buildTest(expect, this)
   }

   private fun KtDotQualifiedExpression.tryExpectWithConfig(): Test? {
      val expect = extractLhsStringArgForDotExpressionWithRhsFinalLambda("expect", "config") ?: return null
      return buildTest(expect, this)
   }

   private fun buildTest(testName: String, element: PsiElement): Test {
      val contexts = element.locateParentTests()
      val path = (contexts.map { it.name } + testName).joinToString(" -- ")
      return Test(testName, path)
   }

   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null

      return when (element) {
         is KtCallExpression -> (element.tryExpect() ?: element.tryContext())?.path
         is KtDotQualifiedExpression -> element.tryExpectWithConfig()?.path
         else -> null
      }
   }

   override fun testPath(element: LeafPsiElement): String? {
      if (!element.isContainedInSpec()) return null

      val ktcall = element.ifCallExpressionNameIdent()
      if (ktcall != null) return testPath(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return testPath(ktdot)

      return null
   }
}
