package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.extractLhsStringArgForDotExpressionWithRhsFinalLambda
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.ifCallExpressionNameIdent
import io.kotest.plugin.intellij.psi.ifDotExpressionSeparator
import io.kotest.plugin.intellij.psi.matchFunction2WithStringAndLambda
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

object BehaviorSpecStyle : SpecStyle {

  override fun generateTest(specName: String, name: String): String {
    return "Given(\"$name\") { }"
  }

  override fun fqn() = FqName("io.kotest.core.spec.style.BehaviorSpec")

  override fun specStyleName(): String = "Behavior Spec"

  // todo this could be optimized to not check for the other parts of the tree until the name is needed
  override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   private val givens = listOf("given", "Given", "`given`", "`Given`")
   private val whens = listOf("when", "When", "`when`", "`When")
   private val thens = listOf("then", "Then", "`then`", "`Then`")

   private fun PsiElement.locateParentTestName(names: List<String>): String? {
      val param = this.matchFunction2WithStringAndLambda(names)
      return if (param == null && parent == null) null else param ?: parent.locateParentTestName(names)
   }

   private fun KtDotQualifiedExpression.tryThenWithConfig(): Test? {
      val then = extractLhsStringArgForDotExpressionWithRhsFinalLambda(thens, listOf("config"))
      return if (then == null) null else {
         val `when` = locateParentTestName(whens)
         val given = locateParentTestName(givens)
         val name = "Given: $given When: $`when` Then: $then"
         Test(then, name)
      }
   }

   private fun KtCallExpression.tryWhen(): Test? {
      val w = this.extractStringArgForFunctionWithStringAndLambdaArgs(whens)
      return if (w == null) null else {
         val given = locateParentTestName(givens)
         val name = "Given: $given When: $w"
         Test(w, name)
      }
   }

   private fun KtCallExpression.tryGiven(): Test? {
      val given = this.extractStringArgForFunctionWithStringAndLambdaArgs(givens)
      return if (given == null) null else {
         val name = "Given: $given"
         Test(given, name)
      }
   }

   private fun KtCallExpression.tryThen(): Test? {
      val then = this.extractStringArgForFunctionWithStringAndLambdaArgs(thens)
      return if (then == null) null else {
         // find the first parent test with a 'when' name
         val `when` = locateParentTestName(whens)
         // find the first parent test with a 'given' name
         val given = locateParentTestName(givens)
         val name = "Given: $given When: $`when` Then: $then"
         Test(then, name)
      }
   }

   override fun test(element: PsiElement): Test? {
      if (!element.isContainedInSpec()) return null

      return when (element) {
         is KtCallExpression -> element.tryGiven() ?: element.tryWhen() ?: element.tryThen()
         is KtDotQualifiedExpression -> element.tryThenWithConfig()
         else -> null
      }
   }

   override fun test(element: LeafPsiElement): Test? {
      if (!element.isContainedInSpec()) return null

      val ktcall = element.ifCallExpressionNameIdent()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
