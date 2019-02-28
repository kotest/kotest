package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

object FeatureSpecStyle : SpecStyle {

  override fun fqn() = FqName("io.kotlintest.specs.FeatureSpec")

  override fun specStyleName(): String = "FeatureSpec"

  // todo this could be optimized to not check for the other parts of the tree until the name is needed
  override fun isTestElement(element: PsiElement): Boolean = testPath(element) != null

  private fun PsiElement.locateParentTests(): List<String> {
    val test = tryAnd() ?: tryFeature()
    val result = if (test == null) emptyList() else listOf(test)
    // if parent is null then we have hit the end
    return if (parent == null) result else parent.locateParentTests() + result
  }

  private fun PsiElement.tryFeature(): String? {
    val feature = matchFunction2WithStringAndLambda(listOf("feature"))
    return if (feature == null) null else "Feature: $feature"
  }

  private fun PsiElement.tryAnd(): String? {
    val and = matchFunction2WithStringAndLambda(listOf("and"))
    return if (and == null) null else "And: $and"
  }

  private fun PsiElement.tryScenario(): String? {
    val scenario = matchFunction2WithStringAndLambda(listOf("scenario"))
    return if (scenario == null) null else "Scenario: $scenario"
  }

  private fun PsiElement.tryScenarioWithConfig(): String? {
    val scenario = extractStringArgForFunctionWithConfig(listOf("scenario"))
    return if (scenario == null) null else "Scenario: $scenario"
  }

  override fun testPath(element: PsiElement): String? {
    if (!element.isContainedInSpec()) return null
    val test = element.tryScenario() ?: element.tryScenarioWithConfig() ?: element.tryAnd() ?: element.tryFeature()
    return if (test == null) null else {
      val paths = element.locateParentTests() + test
      paths.distinct().joinToString(" ")
    }
  }
}