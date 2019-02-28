package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object FunSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.FunSpec")

  override fun specStyleName(): String = "FunSpec"

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.tryTestWithoutConfig() = this.matchFunction2WithStringAndLambda(listOf("test"))
  private fun PsiElement.tryTestWithConfig() = this.extractStringArgForFunctionBeforeDotExpr(listOf("test"),
      listOf("config"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec())
      return null
    return element.tryTestWithoutConfig() ?: element.tryTestWithConfig()
  }
}