package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestPathEntry
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionLambdaOpenBrace
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object BehaviorSpecStyle : SpecStyle {

   override fun generateTest(specName: String, name: String): String {
      return "given(\"$name\") { }"
   }

   override fun fqn() = FqName("io.kotest.core.spec.style.BehaviorSpec")
   override fun specStyleName(): String = "Behavior Spec"

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private val givens = listOf("given", "Given", "`given`", "`Given`")
   private val ands = listOf("and", "And", "`and`", "`And`")
   private val whens = listOf("when", "When", "`when`", "`When`")
   private val thens = listOf("then", "Then", "`then`", "`Then`")

   private fun PsiElement.locateParent(): Test? {
      return when (val p = parent) {
         null -> null
         is KtCallExpression -> p.tryWhen() ?: p.tryAnd() ?: p.tryGiven()
         else -> p.locateParent()
      }
   }

   private fun KtCallExpression.tryGiven(): Test? {
      val given = this.extractStringArgForFunctionWithStringAndLambdaArgs(givens)
      return if (given == null) null else {
         val name = TestName("Given: ", given.text, given.interpolated)
         Test(name, listOf(TestPathEntry(given.text)), TestType.Container, xdisabled = false, root = true, psi = this)
      }
   }

   private fun KtCallExpression.tryWhen(): Test? {
      val w = this.extractStringArgForFunctionWithStringAndLambdaArgs(whens)
      return if (w == null) null else {
         val name = TestName("When: ", w.text, w.interpolated)
         val parents = locateParent()?.path ?: emptyList()
         val path = parents + TestPathEntry(w.text)
         Test(name, path, TestType.Container, xdisabled = false, root = false, psi = this)
      }
   }

   private fun KtCallExpression.tryAnd(): Test? {
      val a = this.extractStringArgForFunctionWithStringAndLambdaArgs(ands)
      return if (a == null) null else {
         val name = TestName("And: ", a.text, a.interpolated)
         val parents = locateParent()?.path ?: emptyList()
         val path = parents + TestPathEntry(a.text)
         Test(name, path, TestType.Container, xdisabled = false, root = false, psi = this)
      }
   }

   private fun KtDotQualifiedExpression.tryThenWithConfig(): Test? {
      val then = extractLhsStringArgForDotExpressionWithRhsFinalLambda(thens, listOf("config"))
      return if (then == null) null else {
         val parents = locateParent()?.path ?: emptyList()
         val name = TestName("Then: ", then.text, then.interpolated)
         val path = parents + TestPathEntry(then.text)
         Test(name, path, TestType.Test, xdisabled = false, root = false, psi = this)
      }
   }

   private fun KtCallExpression.tryThen(): Test? {
      val then = this.extractStringArgForFunctionWithStringAndLambdaArgs(thens)
      return if (then == null) null else {
         val parents = locateParent()?.path ?: emptyList()
         val name = TestName("Then: ", then.text, then.interpolated)
         val path = parents + TestPathEntry(then.text)
         Test(name, path, TestType.Test, xdisabled = false, root = false, psi = this)
      }
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryGiven() ?: element.tryAnd() ?: element.tryWhen() ?: element.tryThen()
         is KtDotQualifiedExpression -> element.tryThenWithConfig()
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
