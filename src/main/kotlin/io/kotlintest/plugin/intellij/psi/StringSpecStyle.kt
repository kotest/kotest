package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object StringSpecStyle : SpecStyle {

  override fun specStyleName(): String = "StringSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.tryTestWithoutConfig(): String? = this.matchStringInvoke()
  private fun PsiElement.tryTestWithConfig(): String? = this.extractLiteralForStringExtensionFunction(listOf("config"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass())
      return null
    println(element.toString() + " " + element.text)
    return element.tryTestWithoutConfig() ?: element.tryTestWithConfig()
  }
}