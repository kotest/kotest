package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement

object FeatureSpecStyle : SpecStyle {

  override fun specStyleName(): String = "FeatureSpec"

  // todo this could be optimized to not check for the other parts of the tree until the name is needed
  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTests(): List<String> {
    val test = tryAnd() ?: tryFeature()
    val result = if (test == null) emptyList() else listOf(test)
    // if parent is null then we have hit the end
    return if (parent == null) result else result + parent.locateParentTests()
  }

  private fun PsiElement.tryFeature(): String? {
    val feature = matchFunction2WithStringAndLambdaArgs(listOf("feature"))
    return if (feature == null) null else "Feature: $feature"
  }

  private fun PsiElement.tryAnd(): String? {
    val and = matchFunction2WithStringAndLambdaArgs(listOf("and"))
    return if (and == null) null else "And: $and"
  }

  private fun PsiElement.tryScenario(): String? {
    val scenario = matchFunction2WithStringAndLambdaArgs(listOf("scenario"))
    return if (scenario == null) null else "Scenario: $scenario"
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isInSpecClass()) return null
    val test = element.tryScenario() ?: element.tryAnd() ?: element.tryFeature()
    return if (test == null) null else test + element.locateParentTests().joinToString(" ", " ", "")
  }
}