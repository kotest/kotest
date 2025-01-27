package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.extractStringForStringExtensionFunctonWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringFromStringInvokeWithLambda
import io.kotest.plugin.intellij.psi.extractStringLiteralFromLhsOfInfixFunction
import io.kotest.plugin.intellij.psi.ifMinusOperator
import io.kotest.plugin.intellij.psi.ifCallExpressionLhsStringOpenQuote
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object FreeSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.FreeSpec")

   override fun specStyleName(): String = "Free Spec"

   override fun generateTest(specName: String, name: String): String {
      return "\"$name\" { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      return when (val p = element.parent) {
         null -> null
         is KtBinaryExpression -> p.tryContainer()
         else -> locateParent(p)
      }
   }

   /**
    * A test of the form:
    *
    *   "test name"{ }
    *
    */
   private fun KtCallExpression.tryTest(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val string = extractStringFromStringInvokeWithLambda() ?: return null
      return buildTest(TestName(null, string.text, string.interpolated), this, TestType.Test, specClass)
   }

   /**
    * Matches tests of the form:
    *
    *   "some test".config(...) {}
    */
   private fun KtDotQualifiedExpression.tryTestWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val string = extractStringForStringExtensionFunctonWithRhsFinalLambda("config") ?: return null
      return buildTest(TestName(null, string.text, string.interpolated), this, TestType.Test, specClass)
   }

   /**
    * Matches tests of the form:
    *
    *   "some test" - {}
    */
   private fun KtBinaryExpression.tryContainer(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val string = extractStringLiteralFromLhsOfInfixFunction(listOf("-")) ?: return null
      return buildTest(TestName(null, string.text, string.interpolated), this, TestType.Container, specClass)
   }

   private fun buildTest(testName: TestName, element: PsiElement, type: TestType, specClass: KtClassOrObject): Test {
      return Test(testName, locateParent(element), specClass, type, false, element)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryTest()
         is KtDotQualifiedExpression -> element.tryTestWithConfig()
         is KtBinaryExpression -> element.tryContainer()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE", "DOT", "MINUS")
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

      val ktbinary = element.ifMinusOperator()
      if (ktbinary != null) return test(ktbinary)

      return null
   }
}
