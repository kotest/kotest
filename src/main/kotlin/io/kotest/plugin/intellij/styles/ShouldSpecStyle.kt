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
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object ShouldSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.ShouldSpec")

   override fun specStyleName(): String = "Should Spec"

   override fun generateTest(specName: String, name: String): String {
      return "should(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private val fnNames = setOf("should", "xshould", "context", "xcontext")

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      val p = element.context ?: return null
      return p.tryKtCallExpression() ?: p.tryKtDotQualifiedExpression() ?: locateParent(p)
   }

   private fun KtCallExpression.tryContext(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, false, specClass)
   }

   /**
    * A test of the form:
    *
    *   should("test name").config(...) { }
    *
    */
   private fun KtDotQualifiedExpression.tryShouldWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val should = extractLhsStringArgForDotExpressionWithRhsFinalLambda("should", "config") ?: return null
      return buildTest(TestName(null, should.text, should.interpolated), this, TestType.Test, false, specClass)
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
    *   should("test name") { }
    *
    */
   private fun KtCallExpression.tryShould(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val should = extractStringArgForFunctionWithStringAndLambdaArgs("should") ?: return null
      return buildTest(TestName(null, should.text, should.interpolated), this, TestType.Test, false, specClass)
   }

   /**
    * A test of the form:
    *
    *   should("test name") { }
    *
    */
   private fun KtCallExpression.tryXShould(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val should = extractStringArgForFunctionWithStringAndLambdaArgs("xshould") ?: return null
      return buildTest(TestName(null, should.text, should.interpolated), this, TestType.Test, true, specClass)
   }

   private fun buildTest(
      testName: TestName,
      element: PsiElement,
      type: TestType,
      xdisabled: Boolean,
      specClass: KtClassOrObject
   ): Test {
      return Test(testName, locateParent(element), specClass, type, xdisabled, element)
   }

   private fun PsiElement.tryKtCallExpression() =
      if (this is KtCallExpression) tryShould() ?: tryXShould() ?: tryContext() else null

   private fun PsiElement.tryKtDotQualifiedExpression() =
      if (this is KtDotQualifiedExpression) tryShouldWithConfig()
         ?: tryContextWithConfig()
         ?: tryXContextWithConfig()
      else null

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryKtCallExpression()
         is KtDotQualifiedExpression -> element.tryKtDotQualifiedExpression()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE", "DOT")
   }

   /**
    * For a FunSpec we consider the following scenarios:
    *
    * should("test name") { }
    * xshould("test name") { }
    * should("test name").config(...) {}
    * xshould("test name").config(...) {}
    * context("test name") {}
    * xcontext("test name") {}
    * context("test name").config(...) {}
    * xcontext("test name").config(...) {}
    */
   override fun test(element: LeafPsiElement): Test? {
      val call = element.ifOpenQuoteOfFunctionName(fnNames)
      if (call != null) return test(call)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
