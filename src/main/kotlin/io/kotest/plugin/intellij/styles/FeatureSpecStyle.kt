package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionLambdaOpenBrace
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FeatureSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FeatureSpec")

   override fun specStyleName(): String = "Feature Spec"

   override fun generateTest(specName: String, name: String): String {
      return "feature(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun PsiElement.locateParentTests(): List<Test> {
      // if parent is null then we have hit the end
      val p = parent ?: return emptyList()
      val context = if (p is KtCallExpression) listOfNotNull(p.tryFeature()) else emptyList()
      return parent.locateParentTests() + context
   }

   private fun KtCallExpression.tryFeature(): Test? {
      val feature = extractStringArgForFunctionWithStringAndLambdaArgs("feature") ?: return null
      val name = "Feature: $feature"
      return buildTest(name, this, TestType.Container)
   }

   private fun KtCallExpression.tryScenario(): Test? {
      val scenario = extractStringArgForFunctionWithStringAndLambdaArgs("scenario") ?: return null
      val name = "Scenario: $scenario"
      return buildTest(name, this, TestType.Test)
   }

   private fun KtDotQualifiedExpression.tryScenarioWithConfig(): Test? {
      val feature = extractLhsStringArgForDotExpressionWithRhsFinalLambda("scenario", "config") ?: return null
      val name = "Scenario: $feature"
      return buildTest(name, this, TestType.Test)
   }

   private fun buildTest(testName: String, element: PsiElement, type: TestType): Test {
      val features = element.locateParentTests()
      val path = (features.map { it.name } + testName).joinToString(" ")
      return Test(testName, path, type)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryScenario() ?: element.tryFeature()
         is KtDotQualifiedExpression -> element.tryScenarioWithConfig()
         else -> null
      }
   }

   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLambdaOpenBrace()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
