package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

object WordSpecStyle : SpecStyle {

  override fun specStyleName(): String = "WordSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  override fun testPath(element: PsiElement): String? {
    if (element is KtBinaryExpression) {
      val children = element.children
      if (children[0] is KtStringTemplateExpression
          && children[1] is KtOperationReferenceExpression && children[1].text == "should"
          && children[2] is KtLambdaExpression
          && element.isInSpecStyle("WordSpec")) {
        return children[0].children[0].text
      }
    } else if (element is KtCallExpression) {
      val children = element.children
      if (children[0] is KtStringTemplateExpression
          && children[1] is KtLambdaArgument
          && element.isInSpecStyle("WordSpec")) {
        return children[0].children[0].text
      }
    }
    return null
  }
}