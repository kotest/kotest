package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object ExpectSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.ExpectSpec")

  override fun specStyleName(): String = "ExpectSpec"

  // todo this could be optimized to not check for the other parts of the tree until the name is needed
  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTests(): List<String> {
    val test = tryContext()
    val result = if (test == null) emptyList() else listOf(test)
    // if parent is null then we have hit the end
    return if (parent == null) result else parent.locateParentTests() + result
  }

  private fun PsiElement.tryExpect() =
      matchFunction2WithStringAndLambda(listOf("expect"))

  private fun PsiElement.tryExpectWithConfig() =
      extractStringArgForFunctionBeforeDotExpr(listOf("expect"), listOf("config"))

  private fun PsiElement.tryContext() =
      matchFunction2WithStringAndLambda(listOf("context"))

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec()) return null
    val test = element.tryExpect() ?: element.tryExpectWithConfig() ?: element.tryContext() ?: return null
    return (element.locateParentTests() + test).distinct().joinToString(" -- ")
  }
}