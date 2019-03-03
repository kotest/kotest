package io.kotlintest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object StringSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.StringSpec")

  override fun specStyleName(): String = "StringSpec"

  override fun generateTest(specName: String, name: String): String {
    return "\"$name\" { }"
  }

  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.tryTestWithoutConfig(): String? = this.matchStringInvoke()
  private fun PsiElement.tryTestWithConfig(): String? = this.extractLiteralForStringExtensionFunction(listOf("config"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec())
      return null
    return element.tryTestWithoutConfig() ?: element.tryTestWithConfig()
  }
}