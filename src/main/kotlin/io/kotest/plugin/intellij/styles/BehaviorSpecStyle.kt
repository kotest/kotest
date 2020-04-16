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

   private fun PsiElement.locateParentTestName(): Test? {
      return when (val p = parent) {
         null -> null
         is KtCallExpression -> p.tryWhen() ?: p.tryGiven()
         else -> p.locateParentTestName()
      }
   }

   private fun KtCallExpression.tryWhen(): Test? {
      val w = this.extractStringArgForFunctionWithStringAndLambdaArgs(whens)
      return if (w == null) null else {
         val name = "When: $w"
         val parent = locateParentTestName()?.path
         val path = "$parent $name"
         Test(name, path, TestType.Container)
      }
   }

   private fun KtCallExpression.tryGiven(): Test? {
      val given = this.extractStringArgForFunctionWithStringAndLambdaArgs(givens)
      return if (given == null) null else {
         val name = "Given: $given"
         Test(name, name, TestType.Container)
      }
   }

   private fun KtDotQualifiedExpression.tryThenWithConfig(): Test? {
      val then = extractLhsStringArgForDotExpressionWithRhsFinalLambda(thens, listOf("config"))
      return if (then == null) null else {
         val parent = locateParentTestName()?.path
         val name = "$parent Then: $then"
         Test(then, name, TestType.Test)
      }
   }

   private fun KtCallExpression.tryThen(): Test? {
      val then = this.extractStringArgForFunctionWithStringAndLambdaArgs(thens)
      return if (then == null) null else {
         val parent = locateParentTestName()?.path
         val name = "$parent Then: $then"
         Test(then, name, TestType.Test)
      }
   }

   override fun test(element: PsiElement): Test? {
      return when (element) {
         is KtCallExpression -> element.tryGiven() ?: element.tryWhen() ?: element.tryThen()
         is KtDotQualifiedExpression -> element.tryThenWithConfig()
         else -> null
      }
   }

   override fun test(element: LeafPsiElement): Test? {
      val ktcall = element.ifCallExpressionNameIdent()
      if (ktcall != null) return test(ktcall)

      val ktdot = element.ifDotExpressionSeparator()
      if (ktdot != null) return test(ktdot)

      return null
   }
}
