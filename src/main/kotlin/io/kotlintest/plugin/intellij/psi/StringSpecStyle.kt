package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object StringSpecStyle : SpecStyle {

  override fun specStyleName(): String = "StringSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass())
      return null
    return element.matchStringInvoke()
  }
}