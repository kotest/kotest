package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

object BehaviorSpecStyle : SpecStyle {

  override fun specStyleName(): String = "BehaviorSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.isBehaviorSpecKeyword(): Boolean = when (text) {
    "given", "Given", "`given`", "`Given`", "then", "Then", "`then`", "`Then`", "when", "When", "`when`", "`When" -> true
    else -> false
  }

  override fun testPath(element: PsiElement): String? {
    if (element is KtCallExpression) {
      val children = element.children
      if (children[0] is KtNameReferenceExpression && children[0].isBehaviorSpecKeyword()
          && children[1] is KtValueArgumentList && children[1].isSingleStringArgList()
          && children[2] is KtLambdaArgument
          && element.isInSpecClass()) {
        return ((children[1] as KtValueArgumentList).children[0] as KtValueArgument).text
      }
    }
    return null
  }
}