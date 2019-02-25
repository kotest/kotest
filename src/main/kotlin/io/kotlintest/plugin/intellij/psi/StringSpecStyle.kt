package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object StringSpecStyle : SpecStyle {

  override fun fqn(): String = "io.kotlintest.specs.StringSpec"

  override fun specStyleName(): String = "StringSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.tryTestWithoutConfig(): String? = this.matchStringInvoke()
  private fun PsiElement.tryTestWithConfig(): String? = this.extractLiteralForStringExtensionFunction(listOf("config"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass())
      return null
    return element.tryTestWithoutConfig() ?: element.tryTestWithConfig()
  }
}