package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionNameIdent
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
      val fullname = "Describe: $name"
      return buildTest(fullname, name.startsWith("!"), this)
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name") { }
    *
    */
   private fun KtCallExpression.tryIt(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("it") ?: return null
      val fullname = "It: $name"
      return buildTest(fullname, name.startsWith("!"), this)
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name") { }
    *
    */
   private fun KtCallExpression.tryXIt(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xit") ?: return null
      val fullname = "xIt: $name"
      return buildTest(fullname, true, this)
   }

   /**
    * Finds tests in the form:
    *
    *   xdescribe("test name") { }
    *
    */
   private fun KtCallExpression.tryXDescribe(): Test? {
      val name = extractStringArgForFunctionWithStringAndLambdaArgs("xdescribe") ?: return null
      val fullname = "xDescribe: $name"
      return buildTest(fullname, true, this)
   }

   /**
    * Finds tests in the form:
    *
    *   it("test name").config { }
    *
    */
   private fun KtDotQualifiedExpression.tryItWithConfig(): Test? {
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("it", "config") ?: return null
      val fullname = "It: $name"
      return buildTest(fullname, name.startsWith("!"), this)
   }

   /**
    * Finds tests in the form:
    *
    *   xit("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXItWithConfig(): Test? {
      val name = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xit", "config") ?: return null
      val fullname = "xIt: $name"
      return buildTest(fullname, true, this)
   }

   private fun buildTest(testName: String, disabled: Boolean, element: PsiElement): Test {
      val contexts = locateParentTests(element)
      val path = (contexts.map { it.name } + testName).joinToString(" ")
      return Test(testName, path, !disabled)
   }

   override fun test(element: PsiElement): Test? {
      if (!element.isContainedInSpec()) return null
      return when (element) {
         is KtCallExpression -> element.tryIt() ?: element.tryXIt() ?: element.tryDescribe() ?: element.tryXDescribe()
         is KtDotQualifiedExpression -> element.tryItWithConfig() ?: element.tryXItWithConfig()
         else -> null
      }
   }

   override fun test(element: LeafPsiElement): Test? {
      if (!element.isContainedInSpec()) return null

      val call = element.ifCallExpressionNameIdent()
      if (call != null) return test(call)

      val dot = element.ifDotExpressionSeparator()
      if (dot != null) return test(dot)

      return null
   }
}
