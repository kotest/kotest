package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionLambdaOpenBrace
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.isDataTestMethodCall
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FeatureSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FeatureSpec")

   override fun specStyleName(): String = "Feature Spec"

   override fun generateTest(specName: String, name: String): String {
      return "feature(\"$name\") { }"
   }

   override fun getDataTestMethodNames(): Set<String> =
      setOf(
         "withData",
         "withFeatures",
         "withScenarios"
      )

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      return when (val p = element.context) {
         null -> null
         is KtCallExpression -> p.tryFeature()
         else -> locateParent(p)
      }
   }

   private fun KtCallExpression.tryFeature(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val feature = extractStringArgForFunctionWithStringAndLambdaArgs("feature") ?: return null
      return buildTest(TestName(null, feature.text, feature.interpolated), this, TestType.Container, specClass)
   }

   private fun KtDotQualifiedExpression.tryFeatureWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val feature = extractLhsStringArgForDotExpressionWithRhsFinalLambda("feature", "config") ?: return null
      return buildTest(TestName(null, feature.text, feature.interpolated), this, TestType.Container, specClass)
   }

   private fun KtCallExpression.tryScenario(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val scenario = extractStringArgForFunctionWithStringAndLambdaArgs("scenario") ?: return null
      return buildTest(TestName(null, scenario.text, scenario.interpolated), this, TestType.Test, specClass)
   }

   private fun KtDotQualifiedExpression.tryScenarioWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val feature = extractLhsStringArgForDotExpressionWithRhsFinalLambda("scenario", "config") ?: return null
      return buildTest(TestName(null, feature.text, feature.interpolated), this, TestType.Test, specClass)
   }

   private fun buildTest(testName: TestName, element: PsiElement, type: TestType, specClass: KtClassOrObject): Test {
      val parent = locateParent(element)
      return Test(testName, parent, specClass, type, false, element)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryScenario() ?: element.tryFeature() ?: element.tryDataTest()
         is KtDotQualifiedExpression -> element.tryFeatureWithConfig() ?: element.tryScenarioWithConfig()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE")
   }

   /**
    * For a FeatureSpec we consider the following scenarios:
    *
    * feature("test name") { }
    * xfeature("test name") { }
    * feature("test name").config(...) {}
    * xfeature("test name").config(...) {}
    * scenario("test name") {}
    * xscenario("test name") {}
    * scenario("test name").config(...) {}
    * xscenario("test name").config(...) {}
    * withData(...) { }
    * withFeatures(...) { }
    * withScenarios(...) { }
    */
   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLambdaOpenBrace()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      // try to find Data Test Method by finding lambda openings
      val dataMethodCall = element.isDataTestMethodCall(getDataTestMethodNames())
      if (dataMethodCall != null) {
         return test(dataMethodCall)
      }

      return null
   }
}
