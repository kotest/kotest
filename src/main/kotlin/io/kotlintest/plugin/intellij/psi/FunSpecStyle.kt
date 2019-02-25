package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object FunSpecStyle : SpecStyle {

  override fun fqn(): String = "io.kotlintest.specs.FunSpec"

  override fun specStyleName(): String = "FunSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.tryTestWithoutConfig() = this.matchFunction2WithStringAndLambda(listOf("test"))
  private fun PsiElement.tryTestWithConfig() = this.extractStringArgForFunctionBeforeDotExpr(listOf("test"),
      listOf("config"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass())
      return null
    return element.tryTestWithoutConfig() ?: element.tryTestWithConfig()
  }
}