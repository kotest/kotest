package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.ifOpenQuoteOfFunctionName
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

   private val contexts = listOf("Context", "context", "`Context`", "`context`")
   private val xcontexts = contexts.map { "x$it" }

   private val givens = listOf("given", "Given", "`given`", "`Given`")
   private val xgivens = givens.map { "x$it" }

   private val ands = listOf("and", "And", "`and`", "`And`")
   private val xands = ands.map { "x$it" }

   private val whens = listOf("when", "When", "`when`", "`When`")
   private val xwhens = whens.map { "x$it" }

   private val thens = listOf("then", "Then", "`then`", "`Then`")
   private val xthens = thens.map { "x$it" }

   private val fnNames = (contexts + xcontexts + givens + xgivens + ands + xands + whens + xwhens + thens + xthens).toSet()

   private fun PsiElement.locateParent(): Test? {
      return when (val p = parent) {
         null -> null
         is KtCallExpression -> p.tryWhen() ?: p.tryXWhen() ?: p.tryAnd() ?: p.tryXAnd() ?: p.tryGiven()
         ?: p.tryXGiven() ?: p.tryContext() ?: p.tryXContext()
         else -> p.locateParent()
      }
   }

   private fun KtCallExpression.tryContext(): Test? {
      val context = this.extractStringArgForFunctionWithStringAndLambdaArgs(contexts)
      return if (context == null) null else {
         val name = TestName("Context: ", context.text, context.interpolated)
         Test(name, null, TestType.Container, xdisabled = false, psi = this)
      }
   }

   private fun KtCallExpression.tryXContext(): Test? {
      val context = this.extractStringArgForFunctionWithStringAndLambdaArgs(xcontexts)
      return if (context == null) null else {
         val name = TestName("Context: ", context.text, context.interpolated)
         Test(name, null, TestType.Container, xdisabled = true, psi = this)
      }
   }

   private fun KtCallExpression.tryGiven(): Test? {
      val given = this.extractStringArgForFunctionWithStringAndLambdaArgs(givens)
      return if (given == null) null else {
         val name = TestName("Given: ", given.text, given.interpolated)
         val parents = locateParent()
         Test(name, parents, TestType.Container, xdisabled = false, psi = this)
      }
   }

   private fun KtCallExpression.tryXGiven(): Test? {
      val given = this.extractStringArgForFunctionWithStringAndLambdaArgs(xgivens)
      return if (given == null) null else {
         val name = TestName("Given: ", given.text, given.interpolated)
         val parents = locateParent()
         Test(name, parents, TestType.Container, xdisabled = true, psi = this)
      }
   }

   private fun KtCallExpression.tryWhen(): Test? {
      val w = this.extractStringArgForFunctionWithStringAndLambdaArgs(whens)
      return if (w == null) null else {
         val name = TestName("When: ", w.text, w.interpolated)
         val parents = locateParent()
         Test(name, parents, TestType.Container, xdisabled = false, psi = this)
      }
   }

   private fun KtCallExpression.tryXWhen(): Test? {
      val w = this.extractStringArgForFunctionWithStringAndLambdaArgs(xwhens)
      return if (w == null) null else {
         val name = TestName("When: ", w.text, w.interpolated)
         val parents = locateParent()
         Test(name, parents, TestType.Container, xdisabled = true, psi = this)
      }
   }

   private fun KtCallExpression.tryAnd(): Test? {
      val a = this.extractStringArgForFunctionWithStringAndLambdaArgs(ands)
      return if (a == null) null else {
         val name = TestName("And: ", a.text, a.interpolated)
         val parents = locateParent()
         Test(name, parents, TestType.Container, xdisabled = false, psi = this)
      }
   }

   private fun KtCallExpression.tryXAnd(): Test? {
      val a = this.extractStringArgForFunctionWithStringAndLambdaArgs(xands)
      return if (a == null) null else {
         val name = TestName("And: ", a.text, a.interpolated)
         val parents = locateParent()
         Test(name, parents, TestType.Container, xdisabled = true, psi = this)
      }
   }

   private fun KtDotQualifiedExpression.tryThenWithConfig(): Test? {
      val then = extractLhsStringArgForDotExpressionWithRhsFinalLambda(thens, listOf("config"))
      return if (then == null) null else {
         val parents = locateParent()
         val name = TestName("Then: ", then.text, then.interpolated)
         Test(name, parents, TestType.Test, xdisabled = false, psi = this)
      }
   }

   private fun KtCallExpression.tryThen(): Test? {
      val then = this.extractStringArgForFunctionWithStringAndLambdaArgs(thens)
      return if (then == null) null else {
         val parents = locateParent()
         val name = TestName("Then: ", then.text, then.interpolated)
         Test(name, parents, TestType.Test, xdisabled = false, psi = this)
      }
   }

   private fun KtCallExpression.tryXThen(): Test? {
      val then = this.extractStringArgForFunctionWithStringAndLambdaArgs(xthens)
      return if (then == null) null else {
         val parents = locateParent()
         val name = TestName("Then: ", then.text, then.interpolated)
         Test(name, parents, TestType.Test, xdisabled = true, psi = this)
      }
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression ->
            element.tryContext() ?: element.tryXContext() ?: element.tryGiven() ?: element.tryXGiven() ?: element.tryAnd() ?: element.tryXAnd() ?: element.tryWhen()
            ?: element.tryXWhen()
            ?: element.tryThen() ?: element.tryXThen()
         is KtDotQualifiedExpression -> element.tryThenWithConfig()
         else -> null
      }
   }

   // for behavior specs we care about an identifier located inside a function
   // with one of the behavior spec names
   override fun isMaybeCanoncialTestLeafElement(element: LeafPsiElement): Boolean {
      if (element.elementType.toString() != "OPEN_QUOTE") return false
//      val context = element.context
//      println(context)
      return false
   }

   override fun possibleLeafElements(): Set<String> {
      return setOf("OPEN_QUOTE", "DOT")
   }

   override fun test(element: LeafPsiElement): Test? {
      val ktcall1 = element.ifOpenQuoteOfFunctionName(fnNames)
      if (ktcall1 != null) return test(ktcall1)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
