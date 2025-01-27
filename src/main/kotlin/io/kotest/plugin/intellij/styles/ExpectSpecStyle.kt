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
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object ExpectSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.style.ExpectSpec")

   override fun specStyleName(): String = "Expect Spec"

   override fun generateTest(specName: String, name: String): String {
      return "expect(\"$name\") { }"
   }

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private fun locateParent(element: PsiElement): Test? {
      // if parent is null then we have hit the end
      return when (val p = element.parent) {
         null -> null
         is KtCallExpression -> p.tryContext()
         else -> locateParent(p)
      }
   }

   private fun KtCallExpression.tryContext(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val context = extractStringArgForFunctionWithStringAndLambdaArgs("context") ?: return null
      return buildTest(TestName(null, context.text, context.interpolated), this, TestType.Container, specClass)
   }

   private fun KtCallExpression.tryExpect(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val expect = extractStringArgForFunctionWithStringAndLambdaArgs("expect") ?: return null
      return buildTest(TestName(null, expect.text, expect.interpolated), this, TestType.Test, specClass)
   }

   private fun KtDotQualifiedExpression.tryExpectWithConfig(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val expect = extractLhsStringArgForDotExpressionWithRhsFinalLambda("expect", "config") ?: return null
      return buildTest(TestName(null, expect.text, expect.interpolated), this, TestType.Test, specClass)
   }

   private fun buildTest(testName: TestName, element: PsiElement, type: TestType, specClass: KtClassOrObject): Test {
      return Test(testName, locateParent(element), specClass, type, false, element)
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryExpect() ?: element.tryContext()
         is KtDotQualifiedExpression -> element.tryExpectWithConfig()
         else -> null
      }
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE")
   }

   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionLambdaOpenBrace()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
