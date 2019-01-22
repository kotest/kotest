package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

object StringSpecStyle : SpecStyle {

  override fun specStyleName(): String = "StringSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  override fun testPath(element: PsiElement): String? {
    if (element is KtCallExpression) {
      val children = element.children
      if (children[0] is KtStringTemplateExpression
          && children[1] is KtLambdaArgument
          && element.isInSpecClass())
        return children[0].children[0].text
    }
    return null
  }
}