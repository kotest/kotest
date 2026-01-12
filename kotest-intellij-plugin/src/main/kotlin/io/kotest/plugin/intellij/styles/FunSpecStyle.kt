package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.ifOpenQuoteOfFunctionName
import io.kotest.plugin.intellij.psi.isDataTestMethodCall
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FunSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FunSpec")

   override fun specStyleName(): String = "Fun Spec"

   override fun generateTest(specName: String, name: String): String {
      return "test(\"$name\") { }"
   }

   override fun getDataTestMethodNames(): Set<String> =
      setOf(
         "withData",
         "withContexts",
         "withTests"
      )

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private val fnNames = setOf("test", "xtest", "context", "xcontext")

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      val p = element.parent ?: return null
      fun tryKtDotQualifiedExpression() =
         if (p is KtDotQualifiedExpression) p.tryContextWithConfig() ?: p.tryXContextWithConfig() else null

      fun tryKtCallExpression() =
         if (p is KtCallExpression) p.tryContext() ?: p.tryXContext() else null
      return tryKtDotQualifiedExpression() ?: tryKtCallExpression() ?: locateParent(p)
   }

   private fun KtCallExpression.tryContext(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, false, specClass)
   }

   /**
    * A test of the form:
    *
    *   xcontext("test name") { }
    *
    */
   private fun KtCallExpression.tryXContext(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("xcontext") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, true, specClass)
   }

   /**
    * A test of the form:
    *
    *   context("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryContextWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val context = extractLhsStringArgForDotExpressionWithRhsFinalLambda("context", "config") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, false, specClass)
   }

   /**
    * A test of the form:
    *
    *   context("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXContextWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val context = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xcontext", "config") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, true, specClass)
   }

   /**
    * A test of the form:
    *
    *   test("test name") { }
    *
    */
   private fun KtCallExpression.tryTest(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val test = extractStringArgForFunctionWithStringAndLambdaArgs("test") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, false, specClass)
   }

   /**
    * A test of the form:
    *
    *   xtest("test name") { }
    *
    */
   private fun KtCallExpression.tryXTest(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val test = extractStringArgForFunctionWithStringAndLambdaArgs("xtest") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, true, specClass)
   }

   /**
    * A test of the form:
    *
    *   test("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val test = extractLhsStringArgForDotExpressionWithRhsFinalLambda("test", "config") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, false, specClass)
   }

   /**
    * A test of the form:
    *
    *   xtest("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryXTestWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val test = extractLhsStringArgForDotExpressionWithRhsFinalLambda("xtest", "config") ?: return null
      return buildTest(TestName(null, test.text, test.interpolated), this, TestType.Test, true, specClass)
   }

   private fun buildTest(
      testName: TestName,
      element: PsiElement,
      type: TestType,
      xdisabled: Boolean,
      specClass: KtClassOrObject,
   ): Test {
      return Test(testName, locateParent(element), specClass, type, xdisabled, element)
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
         is KtCallExpression -> element.tryContext() ?: element.tryXContext() ?: element.tryTest() ?: element.tryXTest() ?: element.tryDataTest()
         is KtDotQualifiedExpression -> element.tryContextWithConfig() ?: element.tryXContextWithConfig()
         ?: element.tryTestWithConfig() ?: element.tryXTestWithConfig()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE", "DOT")
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * test("test name") { }
    * xtest("test name") { }
    * test("test name").config(...) {}
    * xtest("test name").config(...) {}
    * context("test name") {}
    * xcontext("test name") {}
    * context("test name").config(...) {}
    * xcontext("test name").config(...) {}
    * withData(...) { }
    * withContexts(...) { }
    * withTests(...) { }
    */
   override fun test(element: LeafPsiElement): Test? {
      val call = element.ifOpenQuoteOfFunctionName(fnNames)
      if (call != null) return test(call)

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
