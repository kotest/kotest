package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

object ShouldSpecStyle : SpecStyle {

  override fun specStyleName(): String = "ShouldSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  override fun testPath(element: PsiElement): String? {
    if (element is KtCallExpression) {
      val children = element.children
      if (children[0] is KtNameReferenceExpression && children[0].text == "should"
          && children[1] is KtValueArgumentList && children[1].isSingleStringArgList()
          && children[2] is KtLambdaArgument
          && element.isInSpecStyle("ShouldSpec")) {
        return children[1].children[0].children[0].text
      }
    }
    return null
  }
}