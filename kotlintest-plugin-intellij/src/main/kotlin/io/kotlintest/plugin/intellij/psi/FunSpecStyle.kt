package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object FunSpecStyle : SpecStyle {

  override fun specStyleName(): String = "FunSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass())
      return null
    return element.findParameterForFunctionWithLambdaArg(listOf("test"))
  }
}