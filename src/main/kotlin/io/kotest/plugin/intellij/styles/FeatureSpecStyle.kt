package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FeatureSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotest.core.spec.style.FeatureSpec")

  override fun specStyleName(): String = "FeatureSpec"

  override fun generateTest(specName: String, name: String): String {
    return "feature(\"$name\") { }"
  }

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

   private fun PsiElement.locateParentTests(): List<Test> {
      // if parent is null then we have hit the end
      val p = parent ?: return emptyList()
      val context = if (p is KtCallExpression) listOfNotNull(p.tryFeature()) else emptyList()
      return parent.locateParentTests() + context
   }

   private fun KtCallExpression.tryFeature(): Test? {
      val feature = extractStringArgForFunctionWithStringAndLambdaArgs("feature") ?: return null
      val name = "Feature: $feature"
      return buildTest(name, this)
   }

   private fun KtCallExpression.tryScenario(): Test? {
      val scenario = extractStringArgForFunctionWithStringAndLambdaArgs("scenario") ?: return null
      val name = "Scenario: $scenario"
      return buildTest(name, this)
   }

   private fun KtDotQualifiedExpression.tryScenarioWithConfig(): Test? {
      val feature = extractLhsStringArgForDotExpressionWithRhsFinalLambda("scenario", "config") ?: return null
      val name = "Scenario: $feature"
      return buildTest(name, this)
   }

   private fun buildTest(testName: String, element: PsiElement): Test {
      val features = element.locateParentTests()
      val path = (features.map { it.name } + testName).joinToString(" ")
      return Test(testName, path)
   }

   override fun testPath(element: PsiElement): String? {
      if (!element.isContainedInSpec()) return null

      return when (element) {
         is KtCallExpression -> (element.tryScenario() ?: element.tryFeature())?.path
         is KtDotQualifiedExpression -> element.tryScenarioWithConfig()?.path
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
