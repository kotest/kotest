package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionLambdaOpenBrace
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FunSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FunSpec")

   override fun specStyleName(): String = "Fun Spec"

   override fun generateTest(specName: String, name: String): String {
      return "test(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      return when (val p = element.parent) {
         null -> null
         is KtCallExpression -> p.tryContext() ?: p.tryXContext()
         else -> locateParent(p)
      }
   }

   private fun KtCallExpression.tryContext(): Test? {
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, false)
   }

   private fun KtCallExpression.tryXContext(): Test? {
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("xcontext") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, true)
   }

   /**
    * A test of the form:
    *
    *   test("test name") { }
    *
    */
   private fun KtCallExpression.tryTest(): Test? {
      val test = extractStringArgForFunctionWithStringAndLambdaArgs("test") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, false)
   }

   /**
    * A test of the form:
    *
    *   xtest("test name") { }
    *
    */
   private fun KtCallExpression.tryXTest(): Test? {
      val test = extractStringArgForFunctionWithStringAndLambdaArgs("xtest") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, true)
   }

   /**
    * A test of the form:
    *
    *   test("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig(): Test? {
      val test = extractLhsStringArgForDotExpressionWithRhsFinalLambda("test", "config") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, false)
   }

   /**
    * A test of the form:
    *
    *   xtest("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXTestWithConfig(): Test? {
      val test = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xtest", "config") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, true)
   }

   private fun buildTest(testName: TestName, element: PsiElement, type: TestType, xdisabled: Boolean): Test {
      return Test(testName, locateParent(element), type, xdisabled, element)
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    * context("test name").config(...) {}
    */
   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryContext() ?: element.tryXContext() ?: element.tryTest() ?: element.tryXTest()
         is KtDotQualifiedExpression -> element.tryTestWithConfig() ?: element.tryXTestWithConfig()
         else -> null
      }
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * test("test name").config(...) {}
    * context("test name").config(...) {}
    */
   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLambdaOpenBrace()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
