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

object DescribeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.DescribeSpec")

   override fun specStyleName(): String = "Describe Spec"

   override fun generateTest(specName: String, name: String): String {
      return "describe(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   /**
    * For a given PsiElement that we know to be a test, we iterate up the stack looking for parent tests.
    */
   private fun locateParentTests(element: PsiElement): List<Test> {
      // if parent is null then we have hit the end
      val p = element.parent ?: return emptyList()
      val context = if (p is KtCallExpression) listOfNotNull(p.tryDescribe() ?: p.tryXDescribe()) else emptyList()
      return locateParentTests(p) + context
   }

   /**
    * Finds tests in the form:
    *
    *   describe("test name") { }
    *
    */
   private fun KtCallExpression.tryDescribe(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("describe") ?: return null
      return buildTest(name, name.startsWith("!"), this, TestType.Container)
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name") { }
    *
    */
   private fun KtCallExpression.tryIt(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("it") ?: return null
      return buildTest(name, name.startsWith("!"), this, TestType.Test)
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name") { }
    *
    */
   private fun KtCallExpression.tryXIt(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xit") ?: return null
      return buildTest(name, true, this, TestType.Test)
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name") { }
    *
    */
   private fun KtCallExpression.tryXDescribe(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xdescribe") ?: return null
      return buildTest(name, true, this, TestType.Container)
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name").config { }
    *
    */
   private fun KtDotQualifiedExpression.tryItWithConfig(): Test? {
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("it", "config") ?: return null
      return buildTest(name, name.startsWith("!"), this, TestType.Test)
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXItWithConfig(): Test? {
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xit", "config") ?: return null
      return buildTest(name, true, this, TestType.Test)
   }

   private fun buildTest(testName: String, disabled: Boolean, element: PsiElement, testType: TestType): Test {
      val contexts = locateParentTests(element)
      val path = (contexts.map { it.name } + testName).joinToString(" ")
      return Test(testName, path, !disabled, testType, element)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryIt() ?: element.tryXIt() ?: element.tryDescribe() ?: element.tryXDescribe()
         is KtDotQualifiedExpression -> element.tryItWithConfig() ?: element.tryXItWithConfig()
         else -> null
      }
   }

   override fun test(element: LeafPsiElement): Test? {
      val call = element.ifCallExpressionLambdaOpenBrace()
      if (call != null) return test(call)

      val dot = element.ifDotExpressionSeparator()
      if (dot != null) return test(dot)

      return null
   }
}
